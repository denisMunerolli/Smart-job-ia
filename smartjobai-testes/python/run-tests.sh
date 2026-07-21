#!/usr/bin/env bash
# Roda os testes do tailor.py só pelo terminal. Sem instalar nada.
set -e
cd "$(dirname "$0")"
python3 -m unittest test_tailor.py -v
