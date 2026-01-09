#!/usr/bin/env python3
import re
from datetime import datetime
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
TARGET_DIR = ROOT / "src" / "main" / "java"
AUTHOR = "Xintao Hu"

# Existing style: 2025/2/9 16:20
try:
    DATE_STR = datetime.now().strftime("%Y/%-m/%-d %H:%M")
except ValueError:
    DATE_STR = datetime.now().strftime("%Y/%m/%d %H:%M")

author_re = re.compile(r"@Author:\s*.*")
date_re = re.compile(r"@Date:\s*.*")

count_files = 0
count_changes = 0

for path in TARGET_DIR.rglob("*.java"):
    text = path.read_text(encoding="utf-8")
    new_text = text
    new_text = author_re.sub(f"@Author: {AUTHOR}", new_text)
    new_text = date_re.sub(f"@Date: Modified on {DATE_STR}", new_text)

    if new_text != text:
        path.write_text(new_text, encoding="utf-8")
        count_changes += 1
    count_files += 1

print(f"Scanned {count_files} files, updated {count_changes}.")
