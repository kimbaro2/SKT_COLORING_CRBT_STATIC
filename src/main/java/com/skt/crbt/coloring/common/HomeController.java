package com.skt.crbt.coloring.common;

import java.io.IOException;

import java.util.Optional;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.boot.info.GitProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final Environment environment;
    private final Optional<GitProperties> gitProperties;

    public HomeController(Environment environment, Optional<GitProperties> gitProperties) {
        this.environment = environment;
        this.gitProperties = gitProperties;
    }

    @GetMapping("/")
    public void home(HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("EUC-KR");
        response.setContentType("text/html; charset=EUC-KR");

        response.setHeader(
                "Cache-Control",
                "no-store, no-cache, must-revalidate, max-age=0"
        );
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        String gitFooter = GitBuildInfo.footerHtml(environment, gitProperties);
        String gitFooterBlock = gitFooter.isEmpty() ? "" : "<br>" + gitFooter;

        String html = """
        <!DOCTYPE html>
        <html lang="ko">
        <head>
            <meta http-equiv="Content-Type"
                  content="text/html; charset=EUC-KR">
            <meta name="viewport"
                  content="width=device-width, initial-scale=1">
            <title>CRBT Static | Service Portal</title>
            <style>
                :root {
                    --bg: #f5f7fb;
                    --surface: #ffffff;
                    --text: #172033;
                    --muted: #718096;
                    --border: #e7ebf2;
                    --primary: #4f46e5;
                }

                * { box-sizing: border-box; }

                body {
                    margin: 0;
                    min-height: 100vh;
                    background: var(--bg);
                    color: var(--text);
                    font-family: Arial, sans-serif;
                }

                .page {
                    width: calc(100% - 40px);
                    max-width: 1040px;
                    margin: 0 auto;
                    padding: 64px 0 40px;
                }

                .header {
                    display: flex;
                    align-items: center;
                    gap: 16px;
                    margin-bottom: 42px;
                }

                .brand-icon {
                    display: grid;
                    place-items: center;
                    width: 54px;
                    height: 54px;
                    border-radius: 16px;
                    background: #e8eaff;
                    color: var(--primary);
                    font-size: 19px;
                    font-weight: bold;
                }

                .eyebrow {
                    margin: 0 0 6px;
                    color: var(--primary);
                    font-size: 11px;
                    font-weight: bold;
                    letter-spacing: 2px;
                }

                h1 {
                    margin: 0;
                    font-size: 32px;
                    letter-spacing: -1px;
                }

                .subtitle {
                    margin: 10px 0 0;
                    color: var(--muted);
                    font-size: 14px;
                    line-height: 1.8;
                }

                .section-heading {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 18px;
                }

                .section-heading h2 {
                    margin: 0;
                    font-size: 17px;
                }

                .count {
                    color: var(--muted);
                    font-size: 12px;
                }

                .grid {
                    display: grid;
                    grid-template-columns: repeat(2, minmax(0, 1fr));
                    gap: 18px;
                }

                .card {
                    display: flex;
                    flex-direction: column;
                    min-height: 205px;
                    padding: 26px;
                    border: 1px solid var(--border);
                    border-radius: 18px;
                    background: var(--surface);
                    color: inherit;
                    text-decoration: none;
                    box-shadow: 0 3px 12px rgba(20, 30, 60, .025);
                    transition: transform .18s ease,
                                box-shadow .18s ease,
                                border-color .18s ease;
                }

                .card:hover {
                    transform: translateY(-4px);
                    border-color: #c7c9ff;
                    box-shadow: 0 12px 30px rgba(30, 40, 80, .09);
                }

                .card:focus-visible {
                    outline: 3px solid #a5b4fc;
                    outline-offset: 4px;
                }

                .card-top {
                    display: flex;
                    align-items: center;
                    justify-content: space-between;
                }

                .service-icon {
                    display: grid;
                    place-items: center;
                    width: 44px;
                    height: 44px;
                    border-radius: 13px;
                    font-size: 13px;
                    font-weight: bold;
                }

                .cp   { background: #e8edff; color: #4359d8; }
                .asp  { background: #e0f7ef; color: #148568; }
                .rank { background: #fff1d9; color: #b7791f; }
                .gift { background: #f8e7f2; color: #b34488; }

                .arrow {
                    color: #9aa4b5;
                    font-size: 23px;
                    transition: transform .18s ease;
                }

                .card:hover .arrow {
                    transform: translate(3px, -3px);
                    color: var(--primary);
                }

                .card h3 {
                    margin: 23px 0 7px;
                    font-size: 19px;
                }

                .card p {
                    margin: 0;
                    color: var(--muted);
                    font-size: 13px;
                    line-height: 1.7;
                }

                .path {
                    display: inline-block;
                    align-self: flex-start;
                    margin-top: auto;
                    padding-top: 18px;
                    color: #64748b;
                    font-family: monospace;
                    font-size: 11px;
                    overflow-wrap: anywhere;
                }

                .footer {
                    margin-top: 40px;
                    padding-top: 20px;
                    border-top: 1px solid var(--border);
                    color: #9aa4b5;
                    font-size: 12px;
                    line-height: 1.7;
                }

                .footer .git-full {
                    font-family: monospace;
                    font-size: 11px;
                    color: #b0b8c4;
                }

                @media (max-width: 600px) {
                    .page {
                        width: calc(100% - 32px);
                        padding-top: 36px;
                    }

                    .grid {
                        grid-template-columns: 1fr;
                        gap: 12px;
                    }

                    .card {
                        min-height: 185px;
                        padding: 22px;
                    }
                }

                @media (prefers-reduced-motion: reduce) {
                    .card, .arrow {
                        transition: none;
                    }
                    .card:hover {
                        transform: none;
                    }
                }
            </style>
        </head>
        <body>
            <main class="page">
                <header class="header">
                    <div>
                        <p class="eyebrow">서비스 포탈 목록</p>
                        <h1>컬러링 통계</h1>
                    </div>
                </header>

                <section>
                    <div class="grid">
                        <a class="card" href="/cp_static/index.html">
                            <div class="card-top">
                                <div class="service-icon cp">CP</div>
                                <span class="arrow">&#8599;</span>
                            </div>
                            <h3>CRBT CP 통계</h3>
                            <p>CRBT CP 통계 페이지에 접속합니다.</p>
                            <span class="path">/cp_static/</span>
                        </a>

                        <a class="card" href="/aspstatic/index.html">
                            <div class="card-top">
                                <div class="service-icon asp">ASP</div>
                                <span class="arrow">&#8599;</span>
                            </div>
                            <h3>컬러링 통계</h3>
                            <p>컬러링 통계 페이지에 접속합니다.</p>
                            <span class="path">/aspstatic/</span>
                        </a>

                        <a class="card" href="/rankstatic/index.html">
                            <div class="card-top">
                                <div class="service-icon rank">RK</div>
                                <span class="arrow">&#8599;</span>
                            </div>
                            <h3>CRBT 통계 v2</h3>
                            <p>CRBT 통계 페이지에 접속합니다</p>
                            <span class="path">/rankstatic/</span>
                        </a>

                        <a class="card" href="/static/giftgw/giftstatic/index.html">
                            <div class="card-top">
                                <div class="service-icon gift">GF</div>
                                <span class="arrow">&#8599;</span>
                            </div>
                            <h3>선물 통계</h3>
                            <p>선물 통계 페이지에 접속합니다.</p>
                            <span class="path">/static/giftgw/giftstatic/</span>
                        </a>
                    </div>
                </section>

                <footer class="footer">
                    컬러링 통계<!--GIT_FOOTER-->
                </footer>
            </main>
        </body>
        </html>
        """.replace("<!--GIT_FOOTER-->", gitFooterBlock);
        HtmlWriter.write(response, html);
    }
}
