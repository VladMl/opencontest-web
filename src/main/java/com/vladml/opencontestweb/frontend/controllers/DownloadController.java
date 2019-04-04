package com.vladml.opencontestweb.frontend.controllers;

import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@Controller
public class DownloadController {

    @RequestMapping(value = "/files/reports/{file_name}", method = RequestMethod.GET)
    @ResponseBody
    public FileSystemResource downloadReports(@PathVariable("file_name") String fileName,
                                      HttpServletResponse response) {
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=\""+fileName+"\"");
        return new FileSystemResource("files/reports/"+fileName);
    }
}
