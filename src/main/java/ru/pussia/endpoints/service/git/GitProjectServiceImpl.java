package ru.pussia.endpoints.service.git;

import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.pussia.endpoints.entity.details.FileDetails;
import ru.pussia.endpoints.entity.result.ResultModel;
import ru.pussia.endpoints.exception.OutOfBoundReadingException;
import ru.pussia.endpoints.exception.UnknownKeywordException;
import ru.pussia.endpoints.service.analyze.Collector;
import ru.pussia.endpoints.service.segregation.PyFileGetInformationFacade;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GitProjectServiceImpl implements GitProjectService {

    @Value("${repo.dir}")
    private String repoDir;

    private final Logger logger = LoggerFactory.getLogger(GitProjectServiceImpl.class);
    private final PyFileGetInformationFacade pyFileGetInformationFacade;
    private final Collector collector;
    private int cnt = 0;

    private void updateCnt() {
        if (cnt < 0) {
            cnt = 0;
        }

        cnt++;
    }

    private void cloneRepo(String url, String branch, File projectDir) throws GitAPIException {
        logger.info(String.format("Started Cloning repo %s on branch %s", url, branch));

        CloneCommand cloneCommand = Git.cloneRepository()
                .setURI(url)
                .setDirectory(projectDir);
        if (branch != null) {
            cloneCommand.setBranch(branch);
        }

        cloneCommand.call();

        logger.info("Repo cloned successfully");
    }

    @Override
    public ResultModel parseFiles(String url, String branch) throws IOException, GitAPIException, OutOfBoundReadingException, UnknownKeywordException {
        logger.info("Starting parsing repo");

        updateCnt();

        String path = String.format(
                "%s/proj-%s", repoDir, cnt
        );
        int offset = repoDir.length() + 6 + String.valueOf(cnt).length();

        File projectDir = Paths.get(path).toFile();

        cloneRepo(url, branch, projectDir);

        ResultModel result;
        try {
            List<FileDetails> fileDetails = pyFileGetInformationFacade.getInformation(projectDir, offset);

            result = collector.collect(fileDetails);
        } finally {
            Files.walk(projectDir.toPath()) // Delete used project
                    .sorted(Comparator.reverseOrder())
                    .forEach(pth -> {
                        try {
                            Files.delete(pth);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }

        logger.info("Project successfully analyzed");

        return result;
    }
}
