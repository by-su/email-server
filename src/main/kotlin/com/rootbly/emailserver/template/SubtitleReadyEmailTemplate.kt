package com.rootbly.emailserver.template

object SubtitleReadyEmailTemplate {

    private val youtubeIdRegex = Regex(
        """(?:youtube\.com/watch\?(?:.*&)?v=|youtu\.be/|youtube\.com/embed/)([A-Za-z0-9_-]{11})"""
    )

    private fun extractVideoId(url: String): String? =
        youtubeIdRegex.find(url)?.groupValues?.get(1)

    fun render(youtubeUrl: String, message: String): String {
        val videoId = extractVideoId(youtubeUrl)
        val thumbnailUrl = videoId?.let { "https://img.youtube.com/vi/$it/maxresdefault.jpg" }
        val safeMessage = message.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")

        return """
<!DOCTYPE html>
<html lang="ko">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0"></head>
<body style="margin:0;padding:0;background-color:#f7f7f5;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Helvetica,Arial,sans-serif;">
<table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f7f7f5;padding:48px 0 64px;">
  <tr>
    <td align="center">
      <table width="560" cellpadding="0" cellspacing="0" border="0" style="max-width:560px;width:100%;">

        <!-- Logo -->
        <tr>
          <td style="padding:0 0 28px;text-align:left;">
            <span style="font-size:17px;font-weight:700;color:#111111;letter-spacing:-0.3px;">RootblySub</span>
          </td>
        </tr>

        <!-- Card -->
        <tr>
          <td style="background-color:#ffffff;border-radius:16px;padding:40px 40px 36px;border:1px solid #e8e8e4;">

            <!-- Status badge -->
            <table cellpadding="0" cellspacing="0" border="0" style="margin-bottom:24px;">
              <tr>
                <td style="background-color:#f0faf4;border-radius:20px;padding:5px 12px;">
                  <span style="color:#1a7f4e;font-size:12px;font-weight:600;letter-spacing:0.2px;">● 자막 완성</span>
                </td>
              </tr>
            </table>

            <h1 style="margin:0 0 16px;color:#111111;font-size:22px;font-weight:700;line-height:1.4;letter-spacing:-0.4px;">$safeMessage</h1>

            <p style="margin:0 0 28px;color:#666660;font-size:15px;line-height:1.75;">
              요청하신 영상의 자막이 완성됐어요.<br>지금 바로 확인해보세요.
            </p>

            ${if (thumbnailUrl != null) """
            <!-- Thumbnail -->
            <a href="$youtubeUrl" style="display:block;margin-bottom:28px;text-decoration:none;border-radius:10px;overflow:hidden;border:1px solid #e8e8e4;">
              <img src="$thumbnailUrl" alt="영상 썸네일" width="480" style="display:block;width:100%;height:auto;" />
            </a>
            """ else ""}

            <!-- CTA Button -->
            <table cellpadding="0" cellspacing="0" border="0">
              <tr>
                <td style="background-color:#FF0000;border-radius:8px;">
                  <a href="$youtubeUrl" style="display:inline-block;padding:13px 24px;color:#ffffff;font-size:14px;font-weight:600;text-decoration:none;letter-spacing:-0.1px;">YouTube에서 보기</a>
                </td>
              </tr>
            </table>

          </td>
        </tr>

        <!-- Footer -->
        <tr>
          <td style="padding:24px 4px 0;">
            <p style="margin:0;color:#aaaaaa;font-size:12px;line-height:1.6;">
              RootblySub · 이 메일은 자동 발송된 알림입니다.
            </p>
          </td>
        </tr>

      </table>
    </td>
  </tr>
</table>
</body>
</html>
        """.trimIndent()
    }
}
