def conveyorDeploy(name, space, environment, projectName, numberOfInstances = 1) {
    node {
        def spaceUpperCase = space.toUpperCase()
        def nameLowerCase = name.toLowerCase()
        def prodDeploy = spaceUpperCase == 'PROD'
        def sysCredential = "${env.SYS_CREDENTIAL}"
        def stageTitle = projectName == env.PROJECT_NAME ? "Deploy ${nameLowerCase} in ${spaceUpperCase}" : 'Deploy Scenarios Dev';
        def version= projectName == env.PROJECT_NAME ? FULL_VERSION : FULL_VERSION_SCENARIOS;

        withCredentials([
                usernamePassword([credentialsId   : sysCredential,
                                  passwordVariable: 'SYS_PASSWORD',
                                  usernameVariable: 'SYS_USER'])
        ]) {
            stage(stageTitle) {
                step([
                        $class          : 'ConveyorJenkinsPlugin',
                        organization    : "${ORG}",
                        space           : "${spaceUpperCase}",
                        environment     : "${environment}",
                        applicationName : "${projectName}-${nameLowerCase}",
                        serviceNowGroup : "${SERVICE_NOW_GROUP}",
                        serviceNowUserID: "${env.SNOW_AUTHOR}",
                        username        : prodDeploy ? "${env.USERNAME}" : "${env.SYS_USER}",
                        password        : prodDeploy ? "${env.PASSWORD}" : "${env.SYS_PASSWORD}",
                        manifest        : """
                            applications:
                            - memory: 256M
                              instances: ${numberOfInstances}
                              buildpacks:
                                - staticfile_buildpack
                              env:
                                REACT_APP_ENV: "preprod"
                                BUILD_VERSION: "${env.FULL_VERSION}"
                        """
                ])
            }
        }
    }
}
