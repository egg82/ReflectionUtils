package ninja.egg82.reflect;

import com.google.common.collect.Sets;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeElementsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PackageFilter {
    private PackageFilter() {}

    private static final Logger logger = LoggerFactory.getLogger(PackageFilter.class);

    public static <T> List<Class<T>> getClasses(Class<T> clazz, String pkg, boolean recursive, boolean keepInterfaces, boolean keepAbstracts, String... excludePackages) {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz cannot be null.");
        }
        if (pkg == null) {
            throw new IllegalArgumentException("pkg cannot be null.");
        }

        Reflections.log = null;

        String excludeString = null;
        if (excludePackages != null && excludePackages.length > 0) {
            for (int i = 0; i < excludePackages.length; i++) {
                excludePackages[i] = "-" + excludePackages[i];
            }
            excludeString = String.join(", ", excludePackages);
        }

        ConfigurationBuilder config = new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(false),
                        new ResourcesScanner(),
                        new TypeElementsScanner())
                .setUrls(ClasspathHelper.forPackage(pkg, PackageFilter.class.getClassLoader()));

        if (excludeString != null) {
            config = config.filterInputsBy(FilterBuilder.parsePackages(excludeString).include(FilterBuilder.prefix(pkg)));
        } else {
            config = config.filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(pkg)));
        }

        Reflections ref = new Reflections(config);

        Set<String> typeSet = ref.getStore().get("TypeElementsScanner").keySet();
        Set<Class<?>> set = Sets.newHashSet(ReflectionUtils.forNames(typeSet, ref.getConfiguration().getClassLoaders()));
        ArrayList<Class<T>> list = new ArrayList<>();

        for (Class<?> next : set) {
            if (!keepInterfaces && next.isInterface()) {
                continue;
            }
            if (!keepAbstracts && Modifier.isAbstract(next.getModifiers())) {
                continue;
            }

            String n = next.getName();
            n = n.substring(n.indexOf('.') + 1);

            if (n.contains("$")) {
                continue;
            }

            if (!recursive) {
                String p = next.getName();
                p = p.substring(0, p.lastIndexOf('.'));

                if (!p.equalsIgnoreCase(pkg)) {
                    continue;
                }
            }

            if (!clazz.equals(next) && !clazz.isAssignableFrom(next)) {
                continue;
            }

            list.add((Class<T>) next);
        }

        return list;
    }

    public static <T> List<Class<? extends T>> getClassesParameterized(Class<T> clazz, String pkg, boolean recursive, boolean keepInterfaces, boolean keepAbstracts, String... excludePackages) {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz cannot be null.");
        }
        if (pkg == null) {
            throw new IllegalArgumentException("pkg cannot be null.");
        }

        Reflections.log = null;

        String excludeString = null;
        if (excludePackages != null && excludePackages.length > 0) {
            for (int i = 0; i < excludePackages.length; i++) {
                excludePackages[i] = "-" + excludePackages[i];
            }
            excludeString = String.join(", ", excludePackages);
        }

        ConfigurationBuilder config = new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(false),
                        new ResourcesScanner(),
                        new TypeElementsScanner())
                .setUrls(ClasspathHelper.forPackage(pkg, PackageFilter.class.getClassLoader()));

        if (excludeString != null) {
            config = config.filterInputsBy(FilterBuilder.parsePackages(excludeString).include(FilterBuilder.prefix(pkg)));
        } else {
            config = config.filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(pkg)));
        }

        Reflections ref = new Reflections(config);

        Set<String> typeSet = ref.getStore().get("TypeElementsScanner").keySet();
        Set<Class<?>> set = Sets.newHashSet(ReflectionUtils.forNames(typeSet, ref.getConfiguration().getClassLoaders()));
        ArrayList<Class<? extends T>> list = new ArrayList<>();

        for (Class<?> next : set) {
            if (!keepInterfaces && next.isInterface()) {
                continue;
            }
            if (!keepAbstracts && Modifier.isAbstract(next.getModifiers())) {
                continue;
            }

            String n = next.getName();
            n = n.substring(n.indexOf('.') + 1);

            if (n.contains("$")) {
                continue;
            }

            if (!recursive) {
                String p = next.getName();
                p = p.substring(0, p.lastIndexOf('.'));

                if (!p.equalsIgnoreCase(pkg)) {
                    continue;
                }
            }

            if (!clazz.equals(next) && !clazz.isAssignableFrom(next)) {
                continue;
            }

            list.add((Class<? extends T>) next);
        }

        return list;
    }
}
