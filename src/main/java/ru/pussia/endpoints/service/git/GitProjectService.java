package ru.pussia.endpoints.service.git;

import org.eclipse.jgit.api.errors.GitAPIException;
import ru.pussia.endpoints.entity.result.ResultModel;
import ru.pussia.endpoints.exception.OutOfBoundReadingException;
import ru.pussia.endpoints.exception.UnknownKeywordException;

import java.io.IOException;

public interface GitProjectService {

    ResultModel parseFiles(String url, String branch) throws IOException, OutOfBoundReadingException, UnknownKeywordException, GitAPIException;
}
