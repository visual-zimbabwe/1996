#!/usr/bin/env python3
"""Fetch and curate real 1996 magazine covers from Internet Archive.

Outputs a balanced, verified JSON catalog spanning all 12 months of 1996 and
diverse magazine genres (news, music, fashion, film, sports, science, tech,
gaming, culture, and international), tagging SFW/NSFW status cleanly.
"""

from __future__ import annotations

import argparse
import concurrent.futures
import json
import re
import sys
import urllib.parse
import urllib.request
from pathlib import Path
from typing import Any

IA_SEARCH_URL = "https://archive.org/advancedsearch.php"
USER_AGENT = "1996-Magazine-Gallery/1.0 (Android Archival App)"

MONTH_NAMES = [
    "",
    "JANUARY",
    "FEBRUARY",
    "MARCH",
    "APRIL",
    "MAY",
    "JUNE",
    "JULY",
    "AUGUST",
    "SEPTEMBER",
    "OCTOBER",
    "NOVEMBER",
    "DECEMBER",
]

# Diverse genre queries so the 1996 catalog reflects all of 1996, not just tech/gaming.
GENRE_QUERIES: list[tuple[str, str, int]] = [
    # (genre_label, lucene_extra, max_to_keep)
    (
        "News & Culture",
        '(title:("Time" OR "Newsweek" OR "Life" OR "People" OR "Economist" OR "New Yorker" OR "Spiegel" OR "Macleans" OR "Ebony" OR "Jet" OR "Reader\'s Digest" OR "Vanity Fair" OR "George" OR "National Enquirer") OR subject:(news OR politics OR culture OR society))',
        45,
    ),
    (
        "Music",
        '(title:("Rolling Stone" OR "Spin" OR "Vibe" OR "Source" OR "Billboard" OR "Mojo" OR "Select" OR "NME" OR "Melody Maker" OR "Q Magazine" OR "Kerrang" OR "Metal Hammer" OR "Guitar" OR "CMJ" OR "Mixmag" OR "Muzik") OR subject:(music OR rock OR hip-hop OR punk))',
        40,
    ),
    (
        "Fashion & Style",
        '(title:("Vogue" OR "Elle" OR "Bazaar" OR "Esquire" OR "GQ" OR "Details" OR "Face" OR "i-D" OR "Dazed" OR "Marie Claire" OR "Glamour" OR "Cosmopolitan" OR "Seventeen" OR "Sassy" OR "Wallpaper") OR subject:(fashion OR style))',
        35,
    ),
    (
        "Film & TV",
        '(title:("Premiere" OR "Empire" OR "Entertainment Weekly" OR "Sight and Sound" OR "Fangoria" OR "Starlog" OR "Cinefex" OR "TV Guide" OR "Cahiers" OR "Movieline") OR subject:(film OR cinema OR movies OR television))',
        30,
    ),
    (
        "Science & Nature",
        '(title:("National Geographic" OR "Popular Science" OR "Popular Mechanics" OR "Scientific American" OR "Discover" OR "Omni" OR "Astronomy" OR "Sky and Telescope" OR "New Scientist" OR "Smithsonian") OR subject:(science OR nature OR astronomy OR space))',
        30,
    ),
    (
        "Sports & Auto",
        '(title:("Sports Illustrated" OR "Thrasher" OR "Transworld" OR "Car and Driver" OR "Motor Trend" OR "Road and Track" OR "Cycle World" OR "Autocar" OR "FourFourTwo" OR "Slam" OR "Beckett") OR subject:(sports OR skateboarding OR automotive OR basketball OR football OR olympics))',
        30,
    ),
    (
        "Tech & Gaming",
        '(title:("Wired" OR "Macworld" OR "Byte" OR "PC Magazine" OR "Next Generation" OR "Computer Gaming World" OR "Famitsu" OR "Edge" OR "Electronic Gaming" OR "GamePro" OR "Nintendo Power" OR "PlayStation" OR "NetGuide") OR collection:(computermagazines OR videogamemagazines))',
        40,
    ),
    (
        "Comics & Art",
        '(title:("Mad Magazine" OR "Wizard" OR "Heavy Metal" OR "Ray Gun" OR "Emigre" OR "Print" OR "Communication Arts" OR "Juxtapoz" OR "2000 AD" OR "White Dwarf") OR subject:(comics OR graphic design OR art OR illustration))',
        25,
    ),
    (
        "Adult (NSFW)",
        '(title:("Playboy" OR "Penthouse" OR "Hustler" OR "Chic" OR "Oui") OR collection:(pub_playboy OR pub_penthouse OR adultmagazines))',
        18,
    ),
    (
        "General 1996",
        "collection:magazine_rack",
        40,
    ),
]

NSFW_PATTERNS = re.compile(
    r"\b(playboy|penthouse|hustler|oui|cheri|swank|high\s*society|club\s*international|leg\s*show|nude|erotica|adult\s*magazine|xxx|porn)\b",
    re.IGNORECASE,
)

EXCLUDE_TITLE_PATTERNS = re.compile(
    r"\b(index|microfilm|pack|collection|compendium|199[0-5]\s*[-–]\s*1996|1996\s*[-–]\s*199[7-9]|complete\s*set|issues\s*\d+\s*[-–]\s*\d+)\b",
    re.IGNORECASE,
)


def ia_search(query: str, rows: int = 80, sort: str = "downloads desc") -> list[dict[str, Any]]:
    params = [
        ("q", query),
        ("fl[]", "identifier"),
        ("fl[]", "title"),
        ("fl[]", "date"),
        ("fl[]", "collection"),
        ("fl[]", "subject"),
        ("fl[]", "downloads"),
        ("sort[]", sort),
        ("rows", str(rows)),
        ("page", "1"),
        ("output", "json"),
    ]
    url = f"{IA_SEARCH_URL}?{urllib.parse.urlencode(params)}"
    req = urllib.request.Request(url, headers={"User-Agent": USER_AGENT})
    with urllib.request.urlopen(req, timeout=20) as resp:
        data = json.loads(resp.read().decode("utf-8"))
    return data.get("response", {}).get("docs", [])


def parse_1996_month(date_str: str | None, title: str, identifier: str) -> int:
    """Extract month (1-12) or 0 if only year 1996 is known."""
    text = f"{title} {identifier}".lower()
    month_aliases = {
        1: [r"\bjan(?:uary)?\b", r"\b01[-_.]1996\b", r"\b1996[-_.]01\b", r"\b9601\b"],
        2: [r"\bfeb(?:ruary)?\b", r"\b02[-_.]1996\b", r"\b1996[-_.]02\b", r"\b9602\b"],
        3: [r"\bmar(?:ch)?\b", r"\b03[-_.]1996\b", r"\b1996[-_.]03\b", r"\b9603\b"],
        4: [r"\bapr(?:il)?\b", r"\b04[-_.]1996\b", r"\b1996[-_.]04\b", r"\b9604\b"],
        5: [r"\bmay\b", r"\b05[-_.]1996\b", r"\b1996[-_.]05\b", r"\b9605\b"],
        6: [r"\bjun(?:e)?\b", r"\b06[-_.]1996\b", r"\b1996[-_.]06\b", r"\b9606\b"],
        7: [r"\bjul(?:y)?\b", r"\b07[-_.]1996\b", r"\b1996[-_.]07\b", r"\b9607\b"],
        8: [r"\baug(?:ust)?\b", r"\b08[-_.]1996\b", r"\b1996[-_.]08\b", r"\b9608\b"],
        9: [r"\bsep(?:tember|t)?\b", r"\b09[-_.]1996\b", r"\b1996[-_.]09\b", r"\b9609\b"],
        10: [r"\boct(?:ober)?\b", r"\b10[-_.]1996\b", r"\b1996[-_.]10\b", r"\b9610\b"],
        11: [r"\bnov(?:ember)?\b", r"\b11[-_.]1996\b", r"\b1996[-_.]11\b", r"\b9611\b"],
        12: [r"\bdec(?:ember)?\b", r"\b12[-_.]1996\b", r"\b1996[-_.]12\b", r"\b9612\b"],
    }
    for month_idx, patterns in month_aliases.items():
        if any(re.search(pat, text) for pat in patterns):
            return month_idx
    if date_str:
        m = re.match(r"^1996-(\d{2})-(\d{2})", date_str)
        if m:
            month = int(m.group(1))
            day = int(m.group(2))
            if not (month == 1 and day == 1) and 1 <= month <= 12:
                return month
    return 0


def is_nsfw(doc: dict[str, Any]) -> bool:
    title = str(doc.get("title", ""))
    ident = str(doc.get("identifier", ""))
    collections = " ".join(doc.get("collection", []) or [])
    subjects = doc.get("subject", [])
    if isinstance(subjects, list):
        subjects_str = " ".join(str(s) for s in subjects)
    else:
        subjects_str = str(subjects or "")
    combined = f"{title} {ident} {collections} {subjects_str}"
    if "adultmagazines" in collections.lower() or "pub_playboy" in collections.lower() or "pub_penthouse" in collections.lower():
        return True
    return bool(NSFW_PATTERNS.search(combined))


def looks_like_1996_single_issue(doc: dict[str, Any]) -> bool:
    title = str(doc.get("title", "")).strip()
    ident = str(doc.get("identifier", "")).strip()
    collections = set(c.lower() for c in (doc.get("collection") or []))
    if not title or not ident:
        return False
    if "magazine_packs" in collections or "sim_microfilm" in collections:
        return False
    if EXCLUDE_TITLE_PATTERNS.search(title) or EXCLUDE_TITLE_PATTERNS.search(ident):
        return False
    # Reject items clearly from 1995 or 1997 mis-dated as 1996
    years_in_title = re.findall(r"\b(19\d{2}|20\d{2})\b", title)
    if years_in_title and "1996" not in years_in_title:
        return False
    return True


def verify_cover_url(item: dict[str, Any]) -> dict[str, Any] | None:
    ident = item["identifier"]
    cover_url = f"https://archive.org/download/{ident}/page/cover_w600.jpg"
    thumb_url = f"https://archive.org/services/img/{ident}"
    for url in (cover_url, thumb_url):
        req = urllib.request.Request(url, method="HEAD", headers={"User-Agent": USER_AGENT})
        try:
            with urllib.request.urlopen(req, timeout=10) as resp:
                ct = (resp.headers.get("Content-Type") or "").lower()
                if resp.status == 200 and "image/" in ct:
                    if url == thumb_url:
                        # Fallback to thumb if BookReader cover preview is unavailable
                        item["coverUrl"] = thumb_url
                    return item
        except Exception:
            continue
    return None


def main() -> int:
    parser = argparse.ArgumentParser(description="Fetch curated 1996 magazine covers from Internet Archive.")
    parser.add_argument(
        "--out",
        default="app/src/main/assets/covers_1996.json",
        help="Output JSON file path",
    )
    parser.add_argument(
        "--verify",
        action="store_true",
        help="Verify existing output JSON file without re-fetching",
    )
    args = parser.parse_args()
    out_path = Path(args.out)

    if args.verify:
        if not out_path.exists():
            print(f"Missing {out_path}", file=sys.stderr)
            return 1
        data = json.loads(out_path.read_text(encoding="utf-8"))
        assert isinstance(data, list) and len(data) >= 80, f"Expected >= 80 covers, got {len(data)}"
        months = {d["month"] for d in data if d["month"] > 0}
        categories = {d["category"] for d in data}
        print(f"Verified {len(data)} covers across {len(months)} months and {len(categories)} categories.")
        return 0

    candidates: dict[str, dict[str, Any]] = {}
    series_counts: dict[str, int] = {}
    base_filter = 'mediatype:texts AND date:[1996-01-01 TO 1996-12-31] AND collection:magazine_rack AND -collection:magazine_packs AND -collection:sim_microfilm AND -collection:newspapers'

    for genre, extra_q, limit in GENRE_QUERIES:
        if genre == "Adult (NSFW)":
            query = 'mediatype:texts AND date:[1996-01-01 TO 1996-12-31] AND (collection:magazine_rack OR collection:periodicals) AND -collection:magazine_packs AND -collection:sim_microfilm AND ' + extra_q
        elif extra_q == "collection:magazine_rack":
            query = base_filter
        else:
            query = f"{base_filter} AND {extra_q}"
        print(f"Querying [{genre}]...", flush=True)
        try:
            docs = ia_search(query, rows=max(limit * 2, 60))
        except Exception as exc:
            print(f"  Warning: query failed for {genre}: {exc}", file=sys.stderr)
            continue

        kept = 0
        for doc in docs:
            ident = doc.get("identifier")
            if not ident or ident in candidates:
                continue
            if not looks_like_1996_single_issue(doc):
                continue
            title = str(doc.get("title", "")).strip()
            series_key = re.sub(r"[^a-z]+", " ", title.lower()).strip().split()[:2]
            series_prefix = " ".join(series_key)
            if series_prefix and series_counts.get(series_prefix, 0) >= 4:
                continue
            series_counts[series_prefix] = series_counts.get(series_prefix, 0) + 1
            # Clean overly long titles
            if len(title) > 90:
                title = title[:87].rstrip() + "..."
            month = parse_1996_month(doc.get("date"), title, ident)
            date_label = f"{MONTH_NAMES[month]} 1996" if 1 <= month <= 12 else "1996"
            nsfw_flag = is_nsfw(doc)
            # Keep adult titles strictly in the Adult (NSFW) bucket or tagged properly
            if genre != "Adult (NSFW)" and nsfw_flag:
                continue
            candidates[ident] = {
                "identifier": ident,
                "title": title,
                "category": genre if genre != "General 1996" else "General",
                "month": month,
                "dateLabel": date_label,
                "nsfw": nsfw_flag,
                "downloads": int(doc.get("downloads") or 0),
                "thumbUrl": f"https://archive.org/services/img/{ident}",
                "coverUrl": f"https://archive.org/download/{ident}/page/cover_w600.jpg",
                "archiveUrl": f"https://archive.org/details/{ident}",
            }
            kept += 1
            if kept >= limit:
                break
        print(f"  Kept {kept} candidates for {genre}")

    print(f"Verifying HTTP 200 image URLs for {len(candidates)} candidates...", flush=True)
    verified: list[dict[str, Any]] = []
    with concurrent.futures.ThreadPoolExecutor(max_workers=16) as pool:
        for res in pool.map(verify_cover_url, candidates.values()):
            if res is not None:
                verified.append(res)

    # Interleave categories round-robin (by downloads descending within each category)
    # so the initial gallery wall showcases all facets of 1996 immediately.
    by_cat: dict[str, list[dict[str, Any]]] = {}
    for item in verified:
        by_cat.setdefault(item["category"], []).append(item)
    for bucket in by_cat.values():
        bucket.sort(key=lambda x: -x["downloads"])

    cat_order = [
        "News & Culture",
        "Music",
        "Fashion & Style",
        "Film & TV",
        "Science & Nature",
        "Sports & Auto",
        "Tech & Gaming",
        "Comics & Art",
        "General",
        "Adult (NSFW)",
    ]
    interleaved: list[dict[str, Any]] = []
    idx = 0
    while len(interleaved) < len(verified):
        added = False
        for cat in cat_order:
            bucket = by_cat.get(cat)
            if bucket and idx < len(bucket):
                interleaved.append(bucket[idx])
                added = True
        if not added:
            break
        idx += 1
    verified = interleaved

    out_path.parent.mkdir(parents=True, exist_ok=True)
    out_path.write_text(json.dumps(verified, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
    sfw_count = sum(1 for v in verified if not v["nsfw"])
    nsfw_count = sum(1 for v in verified if v["nsfw"])
    print(f"Wrote {len(verified)} verified 1996 covers ({sfw_count} SFW, {nsfw_count} NSFW) to {out_path}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
