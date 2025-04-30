# AI 코드 리뷰 설명서

GitHub Actions를 활용한 자동 코드 리뷰 설정 가이드

## 목차

1. [개요](#개요)
2. [AI 코드 리뷰의 장점](#ai-코드-리뷰의-장점)
3. [설정 방법](#설정-방법)
   - [기본 설정](#기본-설정)
   - [라벨 기반 설정](#라벨-기반-설정)
   - [포크된 PR 설정](#포크된-pr-설정)
4. [사용 가능한 AI 코드 리뷰 액션](#사용-가능한-ai-코드-리뷰-액션)
5. [커스터마이징](#커스터마이징)
   - [지원 모델](#지원-모델)
   - [파일 필터링](#파일-필터링)
   - [언어 설정](#언어-설정)
6. [비용 관리](#비용-관리)
7. [문제 해결](#문제-해결)
8. [최적의 활용 방법](#최적의-활용-방법)
9. [보안 고려사항](#보안-고려사항)

## 개요

AI 코드 리뷰는 인공지능 모델을 활용하여 코드를 자동으로 분석하고 개선 제안을 제공하는 프로세스입니다. GitHub Actions를 통해 PR(Pull Request)이 생성되거나 업데이트될 때마다 자동으로 코드를 검토하고 주석을 달 수 있습니다.

AI 모델(OpenAI의 GPT-4, Anthropic의 Claude 등)이 코드 변경사항을 검토하고 가능한 개선사항, 버그, 보안 취약점, 성능 문제 등에 대한 피드백을 제공합니다.

## AI 코드 리뷰의 장점

1. **시간 절약**: 기본적인 코드 검토를 자동화하여 개발자의 시간을 절약합니다.
2. **일관성**: 휴먼 리뷰어와 달리 AI는 일관된 기준으로 코드를 검토합니다.
3. **빠른 피드백**: PR이 생성되는 즉시 피드백을 받을 수 있습니다.
4. **학습 효과**: 개발자는 AI의 제안을 통해 더 나은 코딩 습관을 기를 수 있습니다.
5. **세밀한 검토**: 사람이 놓치기 쉬운 세부적인 문제까지 발견할 수 있습니다.

## 설정 방법

### 기본 설정

1. GitHub 리포지토리에 `.github/workflows/ai-code-review.yml` 파일을 생성합니다.
2. OpenAI API 키를 획득하고 GitHub Secrets에 저장합니다.
   - GitHub 리포지토리 > Settings > Secrets > Actions > New repository secret
   - 이름: `OPENAI_API_KEY`, 값: 발급받은 API 키
3. 다음과 같은 워크플로우 파일을 작성합니다:

```yaml
name: AI 코드 리뷰

permissions:
  contents: read
  pull-requests: write

on:
  pull_request:
    types: [opened, synchronize]
  pull_request_review_comment:
    types: [created]

jobs:
  review:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Repository
        uses: actions/checkout@v3

      - name: AI 코드 리뷰
        uses: AleksandrFurmenkovOfficial/ai-code-review@v0.8
        with:
          token: ${{ secrets.GITHUB_TOKEN }}
          owner: ${{ github.repository_owner }}
          repo: ${{ github.event.repository.name }}
          pr_number: ${{ github.event.number }}
          
          ai_provider: 'openai'
          openai_api_key: ${{ secrets.OPENAI_API_KEY }}
          openai_model: 'gpt-4o'
          
          include_extensions: '.py,.js,.tsx,.jsx,.ts,.java,.c,.cpp,.go'
          exclude_extensions: '.md,.json,.lock,.yml,.yaml,.gitignore'
          exclude_paths: 'test/,docs/,node_modules/'
```

### 라벨 기반 설정

특정 PR에만 AI 리뷰를 적용하고 싶은 경우, 라벨 기반 트리거를 설정할 수 있습니다:

1. `.github/workflows/label-based-ai-review.yml` 파일을 생성합니다.
2. 다음과 같은 내용으로 워크플로우를 작성합니다:

```yaml
name: 라벨 기반 AI 코드 리뷰

permissions:
  contents: read
  pull-requests: write

on:
  pull_request:
    types: [labeled, opened, synchronize]
  pull_request_review_comment:
    types: [created]

jobs:
  review:
    if: contains(github.event.pull_request.labels.*.name, 'ai-review') || github.event_name == 'pull_request_review_comment'
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Repository
        uses: actions/checkout@v3

      - name: AI 코드 리뷰
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

### 포크된 PR 설정

포크된 레포지토리에서의 PR도 지원하려면 `pull_request_target` 이벤트를 사용해야 합니다. 하지만 이는 보안 위험을 초래할 수 있으므로 신중하게 설정해야 합니다:

```yaml
name: 포크 지원 AI 코드 리뷰

permissions:
  contents: read
  pull-requests: write

on:
  pull_request_target:
    types: [opened, synchronize, reopened]
  pull_request_review_comment:
    types: [created]

jobs:
  review:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Repository
        uses: actions/checkout@v3
        with:
          ref: ${{ github.event.pull_request.head.sha }}

      - name: AI 코드 리뷰
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

## 사용 가능한 AI 코드 리뷰 액션

GitHub Marketplace에는 여러 AI 코드 리뷰 액션이 있습니다. 주요 액션들은 다음과 같습니다:

1. **AleksandrFurmenkovOfficial/ai-code-review** (권장)
   - 다양한 AI 모델 지원 (OpenAI, Anthropic, Google, Deepseek)
   - 세밀한 설정 옵션
   - [GitHub Marketplace 링크](https://github.com/marketplace/actions/ai-code-review)

2. **villesau/ai-codereviewer**
   - 가장 인기 있는 액션 중 하나
   - OpenAI 모델만 지원
   - [GitHub Marketplace 링크](https://github.com/marketplace/actions/ai-code-review-action)

3. **Ostrich-Cyber-Risk/ai-codereviewer**
   - Azure OpenAI 지원
   - [GitHub Marketplace 링크](https://github.com/marketplace/actions/openai-gpt-code-review-action)

4. **Purvesh-Dodiya/AIReviewRadar**
   - 사용자 친화적 UI
   - [GitHub 링크](https://github.com/Purvesh-Dodiya/AIReviewRadar)

## 커스터마이징

### 지원 모델

`AleksandrFurmenkovOfficial/ai-code-review` 액션은 다양한 AI 모델을 지원합니다:

**OpenAI 모델 설정**:
```yaml
ai_provider: 'openai'
openai_api_key: ${{ secrets.OPENAI_API_KEY }}
openai_model: 'gpt-4o'  # 또는 'gpt-4', 'gpt-3.5-turbo' 등
```

**Anthropic 모델 설정**:
```yaml
ai_provider: 'anthropic'
anthropic_api_key: ${{ secrets.ANTHROPIC_API_KEY }}
anthropic_model: 'claude-3-7-sonnet-20250219'  # 또는 다른 Claude 모델
```

**Google 모델 설정**:
```yaml
ai_provider: 'google'
google_api_key: ${{ secrets.GOOGLE_API_KEY }}
google_model: 'gemini-2.0-flash'  # 또는 다른 Gemini 모델
```

**Deepseek 모델 설정**:
```yaml
ai_provider: 'deepseek'
deepseek_api_key: ${{ secrets.DEEPSEEK_API_KEY }}
deepseek_model: 'deepseek-chat'
```

### 파일 필터링

특정 파일만 리뷰하거나 특정 파일을 제외할 수 있습니다:

```yaml
# 특정 파일 확장자만 포함
include_extensions: '.py,.js,.tsx,.ts'

# 특정 파일 확장자 제외
exclude_extensions: '.md,.json,.lock,.yml'

# 특정 경로만 포함
include_paths: 'src/,app/'

# 특정 경로 제외
exclude_paths: 'test/,docs/,node_modules/'
```

### 언어 설정

일부 액션은 언어 설정을 지원합니다. 예를 들어 `anc95/ChatGPT-CodeReview`는 다음과 같이 설정할 수 있습니다:

```yaml
- name: Code Review
  uses: anc95/ChatGPT-CodeReview@main
  env:
    GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
    OPENAI_API_KEY: ${{ secrets.OPENAI_API_KEY }}
    LANGUAGE: Korean
    PROMPT: "한국어로 코드를 검토하고 개선 사항을 제안해주세요."
```

그러나 대부분의 최신 액션은 직접적인 언어 설정 옵션이 없습니다. 이 경우 OpenAI 모델은 기본적으로 코드의 맥락에 따라 응답 언어를 선택합니다.

## 비용 관리

OpenAI API 사용에는 비용이 발생합니다:

1. **토큰 비용**: GPT-4o는 입력 토큰당 약 $0.01, 출력 토큰당 약 $0.03의 비용이 발생합니다.
2. **PR 크기**: 큰 PR일수록 더 많은 토큰을 사용하므로 비용이 증가합니다.

비용 관리 전략:

1. **라벨 기반 접근**: 모든 PR이 아닌 특정 PR에만 AI 리뷰를 적용합니다.
2. **파일 필터링**: 중요한 파일만 리뷰합니다.
3. **저비용 모델 사용**: GPT-4 대신 GPT-3.5와 같은 저비용 모델을 사용합니다.
4. **토큰 제한 설정**: 일부 액션에서는 사용할 토큰 수를 제한할 수 있습니다.

## 문제 해결

### 일반적인 문제

1. **API 키 오류**
   - 문제: 'Invalid API key' 또는 유사한 오류
   - 해결: GitHub Secrets에 API 키가 올바르게 저장되었는지 확인하세요.

2. **토큰 제한 초과**
   - 문제: 'This model's maximum context length is exceeded'
   - 해결: 
     - 파일 필터링을 적용하여 리뷰 범위를 줄이세요.
     - PR 크기를 줄이세요.

3. **권한 문제**
   - 문제: 'Not authorized to post comments'
   - 해결: 워크플로우 파일에 올바른 권한이 설정되었는지 확인하세요.
   ```yaml
   permissions:
     contents: read
     pull-requests: write
   ```

4. **포크된 PR 문제**
   - 문제: 포크된 PR에서 Secret에 접근할 수 없음
   - 해결: `pull_request_target`을 사용하고 신중하게 설정하세요.

### 디버깅 방법

1. **워크플로우 로그 확인**:
   - GitHub > Actions 탭 > 워크플로우 실행 > 로그 확인

2. **디버그 모드 활성화**:
   ```yaml
   - name: AI 코드 리뷰
     uses: AleksandrFurmenkovOfficial/ai-code-review@v0.8
     with:
       # 기타 설정...
       fail_action_if_review_failed: 'true'
   ```

## 최적의 활용 방법

1. **코드 리뷰 가이드라인 수립**:
   - AI 리뷰를 인간 리뷰의 보조 수단으로 활용하세요.
   - 중요한 결정은 항상 인간 리뷰어가 확인해야 합니다.

2. **개발자 교육**:
   - 팀에 AI 리뷰의 장단점을 교육하세요.
   - AI의 제안을 맹목적으로 따르지 않도록 주의하세요.

3. **점진적 도입**:
   - 모든 PR에 바로 적용하기보다는 일부 PR에 먼저 적용하여 효과를 평가하세요.
   - 라벨 기반 접근으로 시작하는 것이 좋습니다.

4. **피드백 수집**:
   - 팀으로부터 AI 리뷰의 유용성에 대한 피드백을 수집하세요.
   - 피드백을 바탕으로 설정을 지속적으로 조정하세요.

## 보안 고려사항

1. **코드 데이터 전송**:
   - 코드가 OpenAI와 같은 외부 서비스로 전송됩니다.
   - 민감한 정보(비밀번호, API 키 등)가 포함된 파일은 제외하세요.

2. **API 키 보호**:
   - API 키는 항상 GitHub Secrets에 저장하세요.
   - API 키의 사용 권한을 제한하세요.

3. **포크된 PR 보안**:
   - `pull_request_target` 사용 시 악의적인 코드가 실행될 위험이 있습니다.
   - 포크된 PR의 코드를 실행하기 전에 항상 코드를 검증하세요.

4. **데이터 프라이버시**:
   - OpenAI의 데이터 사용 정책을 확인하세요.
   - 내부 규정에 따라 특정 코드를 외부 서비스로 전송할 수 있는지 확인하세요.

---

AI 코드 리뷰는 개발 프로세스를 크게 개선할 수 있는 강력한 도구입니다. 하지만 인간 리뷰를 완전히 대체하기보다는 보완하는 수단으로 활용하는 것이 가장 효과적입니다. 신중하게 설정하고 지속적으로 개선하여 팀의 코드 품질을 향상시키세요. 