def call(Map config) {
    def gitUrl = config.gitUrl
    def githubToken = config.githubToken
    def jenkinsUrl = config.jenkinsUrl
    def webhookSecret = config.webhookSecret

    def (githubOwner, repositoryName) = extractGitHubInfo(gitUrl)

    if (!githubOwner || !repositoryName) {
        error "Failed to extract GitHub information from Git URL: ${gitUrl}"
        return false
    }

    def apiUrl = "https://api.github.com/repos/${githubOwner}/${repositoryName}/hooks"

    def payload = [
        name: 'web',
        active: true,
        events: ['push', 'pull_request'],
        config: [
            url: "${jenkinsUrl}/github-webhook/",
            content_type: 'json',
            insecure_ssl: '0',
            secret: webhookSecret
        ]
    ]

    def response = httpRequest(
        url: apiUrl,
        httpMode: 'POST',
        contentType: 'APPLICATION_JSON',
        customHeaders: [[name: 'Authorization', value: "token ${githubToken}"]],
        requestBody: groovy.json.JsonOutput.toJson(payload)
    )

    if (response.status == 201) {
        echo "GitHub webhook created successfully for ${repositoryName}"
        return true
    } else {
        error "Failed to create GitHub webhook. Status: ${response.status}, Response: ${response.content}"
        return false
    }
}

def extractGitHubInfo(String gitUrl) {
    def matcher = gitUrl =~ /(?:https:\/\/github\.com\/|git@github\.com:)([^\/]+)\/([^\/\.]+)(?:\.git)?$/
    if (matcher.find()) {
        return [matcher.group(1), matcher.group(2)]
    }
    return [null, null]
}

return this