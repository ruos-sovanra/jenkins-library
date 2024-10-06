import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import hudson.util.Secret

// Define a static webhook secret
 // Replace with your actual secret

def call(String repoUrl, String webhookUrl, String githubToken) {

    def WEBHOOK_SECRET = '1102b43d4bae5e52ede6fc05ee5dc20e91'
    // Extract owner and repo name from repoUrl
    def repoParts = repoUrl.tokenize('/')
    def owner = repoParts[-2]
    def repo = repoParts[-1].replace('.git', '')

    def apiUrl = "https://api.github.com/repos/${owner}/${repo}/hooks"

    echo "Creating webhook for ${repo} repository..."
    echo "API URL: ${apiUrl}"
    echo "Webhook URL: ${webhookUrl}"
    echo "GitHub Token: ${githubToken}"
    echo "Webhook Secret: ${WEBHOOK_SECRET}"
    echo "Owner: ${owner}"
    echo "Repo: ${repo}"

    // Prepare the webhook configuration payload
    def webhookPayload = JsonOutput.toJson([
        "name"       : "web",
        "active"     : true,
        "events"     : ["push"],
        "config"     : [
            "url"          : webhookUrl,
            "content_type" : "json",
            "insecure_ssl" : "0",
            "secret"       : WEBHOOK_SECRET // Use the static webhook secret here
        ]
    ])

    // Make the request to GitHub's API to create the webhook
    def response = httpRequest(
        url: apiUrl,
        httpMode: 'POST',
        customHeaders: [[name: 'Authorization', value: "Bearer ${githubToken}"]],
        contentType: 'APPLICATION_JSON',
        requestBody: webhookPayload
    )

    // Check if the webhook was created successfully
    def jsonResponse = new JsonSlurper().parseText(response.content)
    if (response.status == 201) {
        echo "Webhook created successfully: ${jsonResponse.url}"
    } else {
        error "Failed to create webhook: ${jsonResponse.message}"
    }
}
