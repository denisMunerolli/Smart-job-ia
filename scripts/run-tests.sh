#!/usr/bin/env bash
# Roda os testes do SmartJobAI (Java + Python) só pelo terminal.
#
# Uso:
#   ./scripts/run-tests.sh              -> Java (unitário, sem Docker) + Python
#   ./scripts/run-tests.sh java          -> só Java, testes unitários (sem Docker)
#   ./scripts/run-tests.sh java-all      -> Java completo, incluindo Testcontainers (precisa Docker)
#   ./scripts/run-tests.sh python        -> só Python

set -e
cd "$(dirname "$0")/.."

rodar_java_unit() {
    echo "=== Java: testes unitários (rápidos, sem Docker) ==="
    if ! command -v mvn &> /dev/null; then
        echo "Maven não encontrado no PATH. Instale: https://maven.apache.org/install.html"
        exit 1
    fi
    mvn -q -pl smartjobai-core -am test -Dtest='*UnitTest' -DfailIfNoTests=false
    echo "OK — testes unitários Java concluídos."
}

rodar_java_all() {
    echo "=== Java: suíte completa, incluindo integração (Testcontainers) ==="
    if ! docker info &> /dev/null; then
        echo "Docker não está rodando — necessário para os testes de integração."
        echo "Suba o Docker (Docker Desktop, colima, etc.) e rode de novo."
        exit 1
    fi
    mvn -q clean test
    echo "OK — suíte completa Java concluída."
}

rodar_python() {
    echo "=== Python: tools/tailor ==="
    (cd tools/tailor && python3 -m unittest test_tailor.py -v)
}

MODO="${1:-tudo}"

case "$MODO" in
    java)       rodar_java_unit ;;
    java-all)   rodar_java_all ;;
    python)     rodar_python ;;
    tudo)       rodar_java_unit; echo; rodar_python ;;
    *)          echo "Uso: ./scripts/run-tests.sh [java|java-all|python|tudo]"; exit 1 ;;
esac
