package site.shanzhao.soil.spring.springboot.launcher;

import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.archive.JarFileArchive;

import java.io.File;
import java.util.Iterator;

/**
  模拟springboot的ExecutableArchiveLauncher#getClassPathArchivesIterator源码，看看它到底会创建哪些Archive
 */
public class FatJarResolveArchiveDemo {
    public static void main(String[] args) throws Exception {
        String fatJarPath = System.getProperty("user.home") + "/IdeaProjects/soil/target/soil-1.0.0.jar";
        JarFileArchive soilJar = new JarFileArchive(new File(fatJarPath));
        Archive.EntryFilter searchFilter = entry -> entry.getName().startsWith("BOOT-INF/");
        // fatjar启动下ExecutableArchiveLauncher.classPathIndex字段为null，ExecutableArchiveLauncher.isEntryIndexed就默认返回true了
        Iterator<Archive> archives = soilJar.getNestedArchives(searchFilter, NESTED_ARCHIVE_ENTRY_FILTER);
        while (archives.hasNext()) {
            System.out.println(archives.next());
        }
        soilJar.close();
    }


    private static final Archive.EntryFilter NESTED_ARCHIVE_ENTRY_FILTER = (entry) -> {
        if (entry.isDirectory()) {
            return entry.getName().equals("BOOT-INF/classes/");
        }
        return entry.getName().startsWith("BOOT-INF/lib/");
    };
}
