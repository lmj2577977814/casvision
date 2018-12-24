package cn.casair.generator.util;

import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

public class GeneratorHelper {

    private GeneratorHelper() {
        throw new RuntimeException("can not create instance");
    }

    public static String packageConvertToPath(String packageName) {
        if(!Strings.isNullOrEmpty(packageName)) {
            return StringUtils.replace(packageName, ".", File.separator);
        }
        return "";
    }

    public static String generatorPath(String basePath, String relativePath) {
        StringBuilder sb = new StringBuilder();
        if(!Strings.isNullOrEmpty(basePath)) {
            sb.append(basePath);
            if(!Strings.isNullOrEmpty(relativePath)) {
                if(!basePath.endsWith(File.separator) && !relativePath.startsWith(File.separator)) {
                    sb.append(File.separator);
                }
            }
        }

        if(!Strings.isNullOrEmpty(relativePath)) {
            sb.append(relativePath);
            if(!relativePath.endsWith(File.separator)) {
                sb.append(File.separator);
            }
        }

        return sb.toString();
    }

}
