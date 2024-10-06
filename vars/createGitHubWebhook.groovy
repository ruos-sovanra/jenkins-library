def createGitHubWebhook(String githubToken, String repoUrl, String webhookUrl, String webhookSecret) {
    def webhookEvents = '["push"]'

    def (userOrg, repoName) = parseGitHubUrl(repoUrl)
    def fullRepoName = "${userOrg}/${repoName}"

    echo "Creating webhook for ${fullRepoName}"

    try {
        def response = httpRequest(
            url: "https://api.github.com/repos/${fullRepoName}/hooks",
            httpMode: 'POST',
            contentType: 'APPLICATION_JSON',
            customHeaders: [[name: 'Authorization', value: "token ${githubToken}"]],
            requestBody: """{
                "name": "web",
                "active": true,
                "events": ${webhookEvents},
                "config": {
                    "url": "${webhookUrl}",
                    "content_type": "json",
                    "secret": "${webhookSecret}",
                    "insecure_ssl": "0"
                }
            }"""
        )

        if (response.status == 201) {
            echo "Webhook successfully created for ${fullRepoName}"
        } else {
            error("Failed to create webhook for ${fullRepoName}: ${response.content}")
        }
    } catch (Exception e) {
        error("Error creating webhook for ${fullRepoName}: ${e.message}")
    }
}

def parseGitHubUrl(String url) {
    def matcher = url =~ /github\.com[:\/](.+)\/(.+?)(?:\.git)?$/
    if (matcher) {
        return [matcher[0][1], matcher[0][2]]
    }
    error("Invalid GitHub URL format: ${url}")
}

