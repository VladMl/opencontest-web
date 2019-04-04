package com.vladml.opencontestweb.backend.services;

import com.vladml.opencontestweb.backend.util.MustacheTemplateHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Service
@Slf4j
public class LangService {

    private final String translationsDir;

    private static final Set<String> knownLangs = new HashSet<String>() {{
        add("en");
        add("ru");
        add("uk");
    }};

    private static Map<String, Map<String, Object>> translations = new HashMap<>();

    @Value("${spring.mustache.prefix}")
    private String templateRoot;

    @Value("${spring.mustache.suffix}")
    private String templateSuffix;

    @Autowired
    MustacheTemplateHelper mustacheTemplateHelper;

    @Autowired
    public LangService(@Value("${app.config.config-dir}") final String configDir) {
        translationsDir = configDir + "lang/";
        try {
            Files.list(Paths.get(translationsDir))
                    .forEach(file -> {
                        String filename = file.getFileName().toString();
                        final Yaml yaml = new Yaml();
                        Map<String, Object> obj = null;
                        try {
                            obj = yaml.load(new FileInputStream(translationsDir + filename));
                            translations.put(filename.replaceFirst("[.][^.]+$", ""), obj);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String normalizeLocale(String locale) {
        if (locale == null) return "en";
        String[] locales = locale.split("_");
        return (locales.length > 0) ? locales[0] : "en";
    }

    public static String getLangFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        } else {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("lang"))
                    return cookie.getValue();
            }
        }
        return null;
    }


    public static String getTranslation(String template, String lang) {
        Map<String, Object> translationsMap;
        translationsMap = translations.get(lang);
        if (translationsMap == null)
            translationsMap = translations.get("en");
            //throw new RuntimeException("Translations for the language : " + lang + " not found.");
        Optional<Object> obj = Optional.ofNullable(translationsMap.get(template));
        return obj.map((s) -> s.toString()).orElse(template);
    }


    private static void addLangToCookie(HttpServletResponse response, String lang) {
        Cookie c = new Cookie("lang", lang);
        c.setPath("/");
        response.addCookie(c);
    }

    public static void setLang(HttpServletRequest request, HttpServletResponse response, String lang) {
        if (lang != null)
            addLangToCookie(response, lang);
        else {
            String lng = LangService.getLangFromCookie(request);
            if (lng == null) {
                lng = LangService.normalizeLocale(request.getLocale().toString());
                if (knownLangs.contains(lng))
                   addLangToCookie(response, LangService.normalizeLocale(request.getLocale().toString()));
                else
                   addLangToCookie(response, "en");
            }
        }
    }

    public static void setLang(HttpServletRequest request, HttpServletResponse response) {
        setLang(request, response, null);
    }

    public static String getLang(HttpServletRequest request, String lang) {
        if ( !lang.equals("") )
            return lang;
        return LangService.normalizeLocale(request.getLocale().toString());
    }


    public Map<String, Object> translateModel(Map<String, Object> model, String lang, String... viewNames) {

        Set<String> variables = new HashSet<String>();
        for (String viewName : viewNames) {
            try {
                //templateRoot.substring(8) +
                variables = mustacheTemplateHelper.getVariablesFromTemplate( templateRoot + viewName + templateSuffix);
                for (String variable : variables) {
                    model.put(variable, getTranslation(variable, lang));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        model.put("lang", lang);
        return model;
    }


}
