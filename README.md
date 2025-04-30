# AI 코드 리뷰 자동화 가이드

GitHub Actions를 활용하여 PR(Pull Request)에 대한 자동 코드 리뷰를 설정하는 방법을 안내합니다. AI 기반 코드 리뷰는 개발자의 시간을 절약하고 코드 품질을 향상시키는 데 도움이 됩니다.

## 목차

- [설정 방법](#설정-방법)
- [지원하는 AI 코드 리뷰 액션 비교](#지원하는-ai-코드-리뷰-액션-비교)
- [OpenAI API 키 설정](#openai-api-키-설정)
- [커스터마이징 옵션](#커스터마이징-옵션)
- [비용 고려사항](#비용-고려사항)
- [액션 사용 예제](#액션-사용-예제)
- [자주 묻는 질문](#자주-묻는-질문)

## 설정 방법

1. GitHub 리포지토리에 `.github/workflows/ai-code-review.yml` 파일을 생성합니다.
2. OpenAI API 키를 GitHub Secrets에 `OPENAI_API_KEY`라는 이름으로 추가합니다.
3. 아래와 같이 워크플로우 파일을 구성합니다:

```yaml
name: AI Code Review

permissions:
  contents: read
  pull-requests: write

on:
  pull_request:
    types: [opened, synchronize]
  # 댓글을 통한 대화 활성화
  pull_request_review_comment:
    types: [created]

concurrency:
  group: ${{ github.repository }}-${{ github.event.number || github.head_ref || github.sha }}-${{ github.workflow }}-${{ github.event_name == 'pull_request_review_comment' && 'pr_comment' || 'pr' }}
  cancel-in-progress: ${{ github.event_name != 'pull_request_review_comment' }}

jobs:
  review:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Repository
        uses: actions/checkout@v3

      # AleksandrFurmenkovOfficial/ai-code-review는 다양한 AI 모델 지원
      - name: AI Code Review
        uses: AleksandrFurmenkovOfficial/ai-code-review@v0.8
        with:
          token: ${{ secrets.GITHUB_TOKEN }}
          owner: ${{ github.repository_owner }}
          repo: ${{ github.event.repository.name }}
          pr_number: ${{ github.event.number }}
          
          # OpenAI API를 사용하여 코드 리뷰 수행
          ai_provider: 'openai'
          openai_api_key: ${{ secrets.OPENAI_API_KEY }}
          openai_model: 'gpt-4o'
          
          # 특정 파일 유형만 리뷰하도록 설정 (선택적)
          include_extensions: '.py,.js,.tsx,.jsx,.ts,.java,.c,.cpp,.go'
          # 마크다운, JSON 파일 등 리뷰에서 제외
          exclude_extensions: '.md,.json,.lock,.yml,.yaml,.gitignore'
          # 특정 경로 제외 (선택적)
          exclude_paths: 'test/,docs/,node_modules/'
```

## 지원하는 AI 코드 리뷰 액션 비교

여러 GitHub Action 중에서 선택할 수 있습니다:

| 액션 | 지원 모델 | 장점 | 단점 |
|------|-----------|------|------|
| [AleksandrFurmenkovOfficial/ai-code-review](https://github.com/marketplace/actions/ai-code-review) | OpenAI, Anthropic, Google, Deepseek | 다양한 AI 모델 지원, 상세한 설정 옵션 | 비교적 신규 액션 |
| [villesau/ai-codereviewer](https://github.com/marketplace/actions/ai-code-review-action) | OpenAI | 많은 사용자, 안정적 | OpenAI만 지원 |
| [Ostrich-Cyber-Risk/ai-codereviewer](https://github.com/marketplace/actions/openai-gpt-code-review-action) | OpenAI | Azure OpenAI 지원 | 적은 커뮤니티 지원 |
| [Purvesh-Dodiya/AIReviewRadar](https://github.com/Purvesh-Dodiya/AIReviewRadar) | OpenAI | 사용자 친화적 UI | 제한된 커스터마이징 |

**권장 액션**: `AleksandrFurmenkovOfficial/ai-code-review`는 다양한 AI 제공업체를 지원하고 설정 옵션이 풍부하여 권장합니다.

## OpenAI API 키 설정

1. [OpenAI 웹사이트](https://platform.openai.com/)에서 계정을 생성합니다.
2. API 키를 생성합니다.
3. GitHub 리포지토리의 `Settings > Secrets > Actions > New repository secret`에서 `OPENAI_API_KEY`라는 이름으로 키를 추가합니다.

## 커스터마이징 옵션

액션의 주요 커스터마이징 옵션은 다음과 같습니다:

- **include_extensions**: 리뷰할 파일 확장자 (예: `.py,.js`)
- **exclude_extensions**: 리뷰에서 제외할 파일 확장자
- **include_paths**: 리뷰할 디렉토리 경로
- **exclude_paths**: 리뷰에서 제외할 디렉토리 경로
- **ai_provider**: AI 제공업체 선택 (openai, anthropic, google, deepseek)
- **openai_model**: 사용할 OpenAI 모델 (예: gpt-4o, gpt-4)

## 비용 고려사항

OpenAI API 사용 시 비용이 발생합니다. GPT-4 사용시 비용이 높을 수 있으므로 다음을 고려하세요:

1. 토큰 제한을 설정하여 비용을 제어합니다.
2. 중요한 PR에만 선택적으로 AI 리뷰를 적용합니다.
3. 비용이 덜 드는 모델(예: GPT-3.5)을 고려합니다.

일반적으로 중간 규모 PR의, GPT-4o를 사용한 AI 코드 리뷰 비용은 약 $0.05~$0.2입니다.

## 액션 사용 예제

### 기본 설정 (OpenAI GPT-4o)

```yaml
- name: AI Code Review
  uses: AleksandrFurmenkovOfficial/ai-code-review@v0.8
  with:
    token: ${{ secrets.GITHUB_TOKEN }}
    owner: ${{ github.repository_owner }}
    repo: ${{ github.event.repository.name }}
    pr_number: ${{ github.event.number }}
    ai_provider: 'openai'
    openai_api_key: ${{ secrets.OPENAI_API_KEY }}
    openai_model: 'gpt-4o'
```

### Anthropic Claude 모델 사용

```yaml
- name: AI Code Review
  uses: AleksandrFurmenkovOfficial/ai-code-review@v0.8
  with:
    token: ${{ secrets.GITHUB_TOKEN }}
    owner: ${{ github.repository_owner }}
    repo: ${{ github.event.repository.name }}
    pr_number: ${{ github.event.number }}
    ai_provider: 'anthropic'
    anthropic_api_key: ${{ secrets.ANTHROPIC_API_KEY }}
    anthropic_model: 'claude-3-7-sonnet-20250219'
```

### 라벨 기반 트리거 설정 (선택적 리뷰)

특정 라벨이 있는 PR에만 리뷰를 수행하려면:

```yaml
on:
  pull_request:
    types: [labeled, opened, synchronize]

jobs:
  review:
    # "ai-review" 라벨이 있는 PR에만 실행
    if: contains(github.event.pull_request.labels.*.name, 'ai-review')
    runs-on: ubuntu-latest
    steps:
      # 액션 설정...
```

## 자주 묻는 질문

### Q: PR이 너무 크면 어떻게 하나요?
A: 대부분의 AI 모델은 토큰 제한이 있습니다. 큰 PR의 경우 `include_paths`를 사용하여 중요한 파일만 리뷰하거나, PR을 작은 단위로 나누는 것이 좋습니다.

### Q: 코드 보안이 걱정됩니다. 코드가 외부로 전송되나요?
A: 예, 코드는 OpenAI나 선택한 AI 제공업체의 서버로 전송됩니다. 중요한 비즈니스 로직이나 민감한 정보가 포함된 코드는 제외하는 것이 좋습니다.

### Q: 포크된 PR에서도 작동하나요?
A: 기본적으로 포크된 PR에서는 보안상의 이유로 GitHub Secrets에 접근할 수 없습니다. `pull_request_target` 이벤트를 사용하면 가능하지만, 보안 위험이 있으므로 신중하게 설정해야 합니다.

### Q: 한국어로 리뷰 결과를 받을 수 있나요?
A: 대부분의 액션은 사용자 지정 프롬프트를 제공하지 않습니다. 그러나 일부 액션에서는 언어 설정이 가능하며, OpenAI 모델은 한국어로 응답할 수 있습니다.

### Q: 오탐지(false positive)가 많이 발생하나요?
A: AI 모델의 특성상 오탐지가 발생할 수 있습니다. 중요한 코드 결정은 항상 인간 리뷰어가 최종 검토해야 합니다.

### Q: 비용을 절감하는 방법이 있나요?
A: 적은 비용의 AI 모델 사용, 리뷰 대상 파일 제한, 라벨 기반 선택적 리뷰 적용 등으로 비용을 절감할 수 있습니다.

---

이 설정으로 GitHub PR에 자동 AI 코드 리뷰를 적용하여 개발 워크플로우를 개선하고 코드 품질을 향상시킬 수 있습니다. 추가 질문이나 문제가 있으시면 이슈를 생성해 주세요.