package com.sbms.greeting;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ApiScanner {
    private final List<String> api;

    private final int apiLevel;

    public ApiScanner() {
        this.api = determineApi();
        this.apiLevel = determineApiLevel(api);
    }

    public List<String> getApi() {
        return api;
    }

    public int getApiLevel() {
        return apiLevel;
    }

    private List<String> determineApi() {
        List<String> api = new ArrayList<>();
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(RestController.class));
        for (BeanDefinition beanDefinition : scanner.findCandidateComponents(this.getClass().getPackage().getName())) {
            try {
                Class<?> beanClass = Class.forName(beanDefinition.getBeanClassName());
                List<String> paths = findAllRequestMappingAnnotatedPaths(beanClass);
                api.addAll(paths);
            }
            catch (ClassNotFoundException e) {
                // This really should not be possible.
                throw new RuntimeException(e);
            }
        }
        Collections.sort(api);
        return api;
    }

    private List<String> findAllRequestMappingAnnotatedPaths(Class<?> beanClass) {
        return Arrays.stream(beanClass.getDeclaredMethods())
                .map(this::extractAnnotatedPaths)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<String> extractAnnotatedPaths(Method method) {
        if (method.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping annotation = method.getAnnotation(RequestMapping.class);
            return Arrays.stream(annotation.path().length == 0 ? annotation.value() : annotation.path())
                    .map(path -> String.format("%s (%s)", path, Arrays.toString(annotation.method())))
                    .collect(Collectors.toList());
        }
        if (method.isAnnotationPresent(GetMapping.class)) {
            GetMapping annotation = method.getAnnotation(GetMapping.class);
            return Arrays.stream(annotation.path().length == 0 ? annotation.value() : annotation.path())
                    .map(path -> String.format("%s (GET)", path))
                    .collect(Collectors.toList());
        }
        if (method.isAnnotationPresent(PostMapping.class)) {
            PostMapping annotation = method.getAnnotation(PostMapping.class);
            return Arrays.stream(annotation.path().length == 0 ? annotation.value() : annotation.path())
                    .map(path -> String.format("%s (POST)", path))
                    .collect(Collectors.toList());
        }
        if (method.isAnnotationPresent(PutMapping.class)) {
            PutMapping annotation = method.getAnnotation(PutMapping.class);
            return Arrays.stream(annotation.path().length == 0 ? annotation.value() : annotation.path())
                    .map(path -> String.format("%s (PUT)", path))
                    .collect(Collectors.toList());
        }
        if (method.isAnnotationPresent(DeleteMapping.class)) {
            DeleteMapping annotation = method.getAnnotation(DeleteMapping.class);
            return Arrays.stream(annotation.path().length == 0 ? annotation.value() : annotation.path())
                    .map(path -> String.format("%s (DELETE)", path))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    private int extractVersionNumber(String path) {
        int startIndex = path.indexOf("/") + 1;
        int endIndex = path.indexOf("/", startIndex);
        if (endIndex < 0) {
            return 0;
        }
        String version = path.substring(startIndex, endIndex).replace("v", "");
        try {
            return Integer.valueOf(version);
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    private int determineApiLevel(List<String> api) {
        return api.stream().mapToInt(this::extractVersionNumber).max().orElse(0);
    }
}
