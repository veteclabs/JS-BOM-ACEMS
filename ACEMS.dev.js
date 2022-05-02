
module.exports = {
    apps: [{
            name: "frame-api",
            cwd: "./bin",
            args: [
                "-jar",
                "frame-api-0.0.1-SNAPSHOT-local.jar" //여기에 war 파일 위치
            ],
            env: {
            },
            script: "C:/\"Program Files\"/Java/jdk-11.0.13/bin/java.exe",

            log_date_format: "YYYY-MM-DD HH:mm Z",
            exec_interpreter: "none",
            exec_mode: "fork"
        }]
}
