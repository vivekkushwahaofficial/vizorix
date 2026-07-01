# AI Core Package (`packages/ai`)

## Purpose
Integrates Large Language Models (LLMs) to generate human-readable descriptions of step-by-step code execution.

## Responsibility
- Serializes timeline memory updates into compact prompts.
- Invokes API connectors for OpenAI, Anthropic Claude, and Ollama.
- Manages cost controls through prompt optimization.

## Planned Public Interfaces
- `generateStepExplanation(code: string, currentFrame: TimelineFrame, nextFrame: TimelineFrame): Promise<string>`
- `configureModel(provider: string, config: ModelConfig): void`

## Dependencies
- None
