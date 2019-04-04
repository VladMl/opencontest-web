package com.vladml.opencontestweb.backend.util;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MustacheTemplateHelper {

    private static final String PATTERN_VARIABLE = "\\{\\{(.*?)\\}\\}";
    private static final String PATTERN_NESTED_TEMPLATE = "\\{\\{>(.*?)\\}\\}";

    @Value("${spring.mustache.suffix}")
    private String templateSuffix;


    private static Set<String> getNestedTemplates(String template) throws IOException {
        Set<String> list = new HashSet<>();
        String content = new String(Files.readAllBytes(Paths.get(URI.create(template.replace("\\","/")))));
        Matcher m = Pattern.compile(PATTERN_NESTED_TEMPLATE).matcher(content);
        while (m.find())
            list.add(m.group(1).trim());
        return list;
    }

    private static void parseTemplate(String template, Set<String> variables) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(URI.create(template.replace("\\","/")))));
        Matcher m = Pattern.compile(PATTERN_VARIABLE).matcher(content);
        while (m.find()) {
            String variable = m.group(1).trim();
            if (variable != null && !variable.isEmpty())
                if (variable.charAt(0) != '#' && variable.charAt(0) != '>' && variable.charAt(0) != '/')
                    variables.add(m.group(1).trim());
        }
    }

    public Set<String> getVariablesFromTemplate(String template) throws IOException {
        Set<String> variables = new HashSet<>();
        parseTemplate(template, variables);
        for (String nestedTemplate : getNestedTemplates(template)) {
            parseTemplate(FilenameUtils.getPath(template) + nestedTemplate + templateSuffix, variables);
        }
        return variables;
    }


}
