package cn.casair.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Casvision.
 * <p>
 * Properties are configured in the application.yml file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
    private GeneratorProperties generator;

    public GeneratorProperties getGenerator() {
        return generator;
    }

    public void setGenerator(GeneratorProperties generator) {
        this.generator = generator;
    }

    public static class GeneratorProperties {
        private String basePath;
        private String domainPackage;
        private String repositoryPackage;
        private String repositoryXmlPathRela;
        private String servicePackage;
        private String serviceImplPackage;
        private String dtoPackage;
        private String dtoMapperPackage;
        private String resourcePackage;

        public String getBasePath() {
            return basePath;
        }

        public void setBasePath(String basePath) {
            this.basePath = basePath;
        }

        public String getDomainPackage() {
            return domainPackage;
        }

        public void setDomainPackage(String domainPackage) {
            this.domainPackage = domainPackage;
        }

        public String getRepositoryPackage() {
            return repositoryPackage;
        }

        public void setRepositoryPackage(String repositoryPackage) {
            this.repositoryPackage = repositoryPackage;
        }

        public String getRepositoryXmlPathRela() {
            return repositoryXmlPathRela;
        }

        public void setRepositoryXmlPathRela(String repositoryXmlPathRela) {
            this.repositoryXmlPathRela = repositoryXmlPathRela;
        }

        public String getServicePackage() {
            return servicePackage;
        }

        public void setServicePackage(String servicePackage) {
            this.servicePackage = servicePackage;
        }

        public String getServiceImplPackage() {
            return serviceImplPackage;
        }

        public void setServiceImplPackage(String serviceImplPackage) {
            this.serviceImplPackage = serviceImplPackage;
        }

        public String getDtoPackage() {
            return dtoPackage;
        }

        public void setDtoPackage(String dtoPackage) {
            this.dtoPackage = dtoPackage;
        }

        public String getDtoMapperPackage() {
            return dtoMapperPackage;
        }

        public void setDtoMapperPackage(String dtoMapperPackage) {
            this.dtoMapperPackage = dtoMapperPackage;
        }

        public String getResourcePackage() {
            return resourcePackage;
        }

        public void setResourcePackage(String resourcePackage) {
            this.resourcePackage = resourcePackage;
        }
    }
}
