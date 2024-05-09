package ru.pussia.endpoints.controller;

import lombok.AllArgsConstructor;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.pussia.endpoints.entity.result.ResultModel;
import ru.pussia.endpoints.exception.OutOfBoundReadingException;
import ru.pussia.endpoints.exception.UnknownKeywordException;
import ru.pussia.endpoints.service.git.GitProjectService;

import java.io.IOException;

@RestController
@AllArgsConstructor
public class GitProjectController {

    private final GitProjectService gitProjectService;

    @CrossOrigin
    @PostMapping("/api/v1/projects")
    public ResponseEntity<ResultModel> getProject(
            @RequestParam String link,
            @RequestParam(required = false) String branch
            ) throws IOException, GitAPIException, OutOfBoundReadingException, UnknownKeywordException {
        ResultModel result = gitProjectService.parseFiles(link, branch);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
