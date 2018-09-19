package com.sbms.greeting;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ApiScanner {
    private final int apiVersion;

    public ApiScanner() {
        this.apiVersion = determineApiVersion();
    }

    public int getApiLevel() {
        return apiVersion;
    }

    private int determineApiVersion() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(RestController.class));
        List<String> paths = new ArrayList<>();
        for (BeanDefinition beanDefinition : scanner.findCandidateComponents(this.getClass().getPackage().getName())) {
            try {
                Class<?> beanClass = Class.forName(beanDefinition.getBeanClassName());
                paths.addAll(findAllRequestMappingAnnotatedPaths(beanClass));
            }
            catch (ClassNotFoundException e) {
                // This really should not be possible.
                throw new RuntimeException(e);
            }
        }
        return findHighestVersionInPaths(paths);
    }

    private List<String> findAllRequestMappingAnnotatedPaths(Class<?> beanClass) {
        return Arrays.stream(beanClass.getDeclaredMethods())
                .map(this::extractAnnotatedPaths)
                .flatMap(Arrays::stream)
                .collect(Collectors.toList());
    }

    private String[] extractAnnotatedPaths(Method method) {
        if (method.isAnnotationPresent(RequestMapping.class)) {
            return method.getAnnotation(RequestMapping.class).path();
        }
        if (method.isAnnotationPresent(GetMapping.class)) {
            return method.getAnnotation(GetMapping.class).path();
        }
        if (method.isAnnotationPresent(PostMapping.class)) {
            return method.getAnnotation(PostMapping.class).path();
        }
        if (method.isAnnotationPresent(PutMapping.class)) {
            return method.getAnnotation(PutMapping.class).path();
        }
        if (method.isAnnotationPresent(DeleteMapping.class)) {
            return method.getAnnotation(DeleteMapping.class).path();
        }
        return new String[] { "/v0/" };
    }

    private int findHighestVersionInPaths(List<String> paths) {
        return paths.stream()
                .mapToInt(this::extractVersionNumber)
                .max()
                .orElse(0);
    }

    private int extractVersionNumber(String path) {
        int startIndex = path.indexOf("/") + 1;
        int endIndex = path.indexOf("/", startIndex);
        String version = path.substring(startIndex, endIndex).replace("v", "");
        try {
            return Integer.valueOf(version);
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }
}
