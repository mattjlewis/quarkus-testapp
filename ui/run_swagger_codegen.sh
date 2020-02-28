#!/usr/bin/env bash
swagger-codegen generate -i http://localhost:9090/openapi -l typescript-angular -o src/app/shared
