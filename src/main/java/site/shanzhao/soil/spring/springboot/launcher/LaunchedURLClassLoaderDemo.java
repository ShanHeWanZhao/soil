package site.shanzhao.soil.spring.springboot.launcher;

import org.junit.jupiter.api.Assertions;
import org.springframework.boot.loader.LaunchedURLClassLoader;
import org.springframework.boot.loader.jar.JarFile;
import org.springframework.util.Assert;

import java.io.File;
import java.net.URL;

public class LaunchedURLClassLoaderDemo {

    public static void main(String[] args) throws Exception {
        String fatJarPath = System.getProperty("user.home") + "/IdeaProjects/soil/target/soil-1.0.0.jar";
        File fatJar = new File( fatJarPath);
        Assert.isTrue(fatJar.exists(), "Fat jar not found");
        JarFile jarFile = new JarFile(fatJar);
        JarFile nestedJar = jarFile.getNestedJarFile(jarFile.getJarEntry("BOOT-INF/lib/hutool-json-5.8.25.jar"));
        // 内部会直接创建org.springframework.boot.loader.jar.handler，所以在这里可以先不注册java.protocol.handler.pkgs
        URL jsonUrl = nestedJar.getUrl();
        // ======== 此时必须注册jar包处理器org.springframework.boot.loader.jar.handler，才能读取fatjar =========
        JarFile.registerUrlProtocolHandler();
        // eg: jar:file:/Users/reef/IdeaProjects/soil/target/soil-1.0.0.jar!/BOOT-INF/lib/hutool-core-5.8.25.jar!/
        URL coreUrl = new URL("jar:file:" + fatJarPath + "!/BOOT-INF/lib/hutool-core-5.8.25.jar!/");
        // 创建 LaunchedURLClassLoader
        ClassLoader appClassLoader = LaunchedURLClassLoaderDemo.class.getClassLoader();
        try (LaunchedURLClassLoader classLoader = new LaunchedURLClassLoader(new URL[]{jsonUrl, coreUrl}, appClassLoader)) {
            String coreJarClass = "cn.hutool.core.thread.AsyncUtil";
            String jsonJarClass = "cn.hutool.json.JSONUtil";
            // 确保当前运行环境的classpath没有指定的class
            Assertions.assertThrows(ClassNotFoundException.class, () -> appClassLoader.loadClass(coreJarClass));
            Assertions.assertThrows(ClassNotFoundException.class, () -> appClassLoader.loadClass(jsonJarClass));
            // 使用自定义的LaunchedURLClassLoader加载class
            loadAndAssert(classLoader, coreJarClass);
            loadAndAssert(classLoader, jsonJarClass);
        }
        jarFile.close();
    }


    private static void loadAndAssert(ClassLoader loader, String className) throws Exception {
        Class<?> clazz = loader.loadClass(className);
        Assertions.assertNotNull(clazz);
        Assertions.assertSame(loader, clazz.getClassLoader(), "Class not loaded from expected classloader");
        System.out.printf("Loaded class: %-40s → %s%n", className, clazz.getProtectionDomain().getCodeSource().getLocation());
    }

}
