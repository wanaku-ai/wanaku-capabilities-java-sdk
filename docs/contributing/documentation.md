---
title: Write SDK documentation
description: Page conventions for the Wanaku Capabilities Java SDK docs, with sections, slugs, frontmatter, page skeleton, links, and the checks that CI runs.
---

# Write SDK documentation

This page describes the shape of an SDK documentation page and the checks that CI runs on the `docs/` tree. These rules are the same as in the [Wanaku documentation conventions](https://github.com/wanaku-ai/wanaku/blob/main/docs/contributing/documentation.md). Use ASD-STE100 Simplified Technical English for technical content, and plain technical English for introductions and framing text.

## Sections

The `docs/` tree has one folder for each reader task:

| Folder | Reader task |
|---|---|
| `get-started/` | Add the SDK to a project. |
| `guides/` | Do a task with one or more SDK modules. |
| `reference/` | Look up modules, types, and classes. |
| `contributing/` | Build, test, document, and release the SDK. |

Each folder has an `index.md` hub. The hub lists every page in its folder. Keep a hub at 70 lines or fewer.

## Files and slugs

- Use lowercase kebab-case file names with four words or fewer. The folder supplies the group name: use `reference/modules.md`, not `reference/reference-modules.md`.
- Use a verb-object slug for a tutorial or a how-to, for example `download-maven-artifacts.md`. Use a noun for a reference or a concept page, for example `modules.md`.
- Do not put version numbers or a `-guide` suffix in new slugs.
- A slug is a published URL. When you move or rename a page, add a line to `docs/redirects.txt` in the same pull request. Each line has the old slug and the new slug.

## Frontmatter

Each page starts with a `title` and a `description`:

```markdown
---
title: Modules
description: The Maven modules of the Wanaku Capabilities Java SDK, with the coordinates, purpose, and main packages and classes of each published artifact.
---
```

- `title` has 60 characters or fewer and is unique. Do not add a site suffix. The site adds it.
- `description` has 120 to 155 characters and is unique. Write it in short, active sentences.
- If a value contains a colon followed by a space, put the value in double quotes. Otherwise, the frontmatter is not valid YAML.

## Page skeleton

```markdown
# H1 with the same meaning as the title

One to three sentences: what the reader can do after this page.

## Prerequisites (omit when empty)

## Body sections (specific H2s, never "Overview")

## Next steps (two to four links, nothing after this section)
```

Use one page type for each page:

| Type | H1 | Body |
|---|---|---|
| Tutorial | "Build a Camel-based capability" | Numbered steps with one visible result. |
| How-to | Verb first: "Download Maven artifacts" | Goal, steps, and verification. |
| Reference | Noun: "Modules" | Tables and schemas, in alphabetical or schema order. |
| Explanation | Noun: "Architecture" | The model, a diagram, and the limits. |

Keep tutorials and how-tos at 350 lines or fewer, explanations at 500, and references at 800. Split a longer page by task.

## Links

- Use relative links with the `.md` extension, for example `../reference/modules.md`.
- Do not leave `docs/` with a relative link. The site publishes only the `docs/` tree. Link code and example files with `https://github.com/wanaku-ai/wanaku-capabilities-java-sdk/blob/main/<path>`.
- Link fragments only to H2 and H3 headings.
- Use the name of the destination page as the link text. Do not use "here".
- End each page with one "Next steps" section. Do not use "Related Docs".
- Add each page to `docs/nav.json` exactly once. The website builds the sidebar from this file.
- Do not write `{{` in prose. The website parses it as a Vue expression. Put it in a code block or in inline code.

## Headings and callouts

- Use one H1. Do not skip heading levels. Stop at H3.
- Use sentence case for new and renamed headings. Do not rename headings in a move-only change, because that breaks anchors.
- Do not number headings. Renumbering breaks anchors.
- Expand acronyms on first use in an H1 or a description, for example BOM (Bill of Materials).
- Use GitHub alert syntax for callouts: `> [!NOTE]`, `> [!IMPORTANT]`, and `> [!WARNING]`.
- Do not write a "Contents" list. GitHub and the site show an outline.

## Check your changes

CI runs these checks on each pull request that changes Markdown or the docs navigation. Run them locally before you push:

```bash
python3 .github/check-docs-nav.py
npx markdownlint-cli2
lychee --offline --include-fragments --exclude-path .oss-ai-helper-rules --exclude-path target '**/*.md'
```

`.github/check-docs-nav.py` checks the rules on this page:

- Each page is in `docs/nav.json` exactly once, and each `nav.json` link has a page.
- Each page is linked from its folder hub.
- Each page has a `title` and a `description` of the correct length, and no other page has the same values.
- No relative link leaves `docs/`.
- No page has a "Related Docs" heading.
- No page uses a bold callout such as `> **Note:**`. Use GitHub alert syntax.
- Each `docs/redirects.txt` target has a page.

## Next steps

- [Contributing](index.md): build and test the SDK.
- [Release guide](release.md): release a new version.
