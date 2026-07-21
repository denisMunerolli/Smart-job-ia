#!/usr/bin/env bash
# Roda os testes do SmartJobAI só pelo terminal, sem abrir Eclipse/VS Code.
#
# Uso:
#   ./run-tests.sh          -> só os testes unitários rápidos (sem Docker)
#   ./run-tests.sh all      -> todos os testes, incluindo integração com
#                              Testcontainers (precisa do Docker rodando)

set -e

MODO="${1:-unit}"

if ! command -v mvn &> /dev/null; then
    echo "Maven não encontrado no PATH. Instale: https://maven.apache.org/install.html"
    exit 1
fi

if ! command -v java &> /dev/null; then
    echo "Java não encontrado no PATH. Instale o JDK 21."
    exit 1
fi

if [ "$MODO" = "unit" ]; then
    echo "=== Testes unitários (rápidos, sem Docker) ==="
    mvn -q -pl smartjobai-core -am test -Dtest='*UnitTest' -DfailIfNoTests=false
    echo "OK — testes unitários concluídos."

elif [ "$MODO" = "all" ]; then
    if ! docker info &> /dev/null; then
        echo "Docker não está rodando — os testes de integração usam Testcontainers"
        echo "e precisam de um daemon Docker ativo (Docker Desktop, colima, etc.)."
        echo "Suba o Docker e rode de novo: ./run-tests.sh all"
        exit 1
    fi
    echo "=== Todos os testes, incluindo integração (Testcontainers) ==="
    mvn -q clean test
    echo "OK — suíte completa concluída."

else
    echo "Uso: ./run-tests.sh [unit|all]"
    exit 1
fi
