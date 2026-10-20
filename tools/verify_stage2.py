"""Verify the extracted baseline and curated catalog without Android or network."""

import hashlib
import json
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
DOCS = ROOT / 'docs/migration'
BASELINE = '3cd7cf2e984af5c028bfb6cb355dd023f14e09f9'
CATALOG = 'app/src/main/assets/communication/communication_catalog_v1.json'


def require(condition, message):
    if not condition:
        raise ValueError(message)


def read_json(path):
    return json.loads(path.read_text(encoding='utf-8-sig'))


def verify():
    manifest = read_json(DOCS / 'EXTRACCION_ETAPA_2.json')
    require(manifest['sourceCommit'] == BASELINE, 'Wrong source baseline')
    records = manifest['files']
    paths = [r['destination'] for r in records]
    require(len(paths) == len(set(paths)), 'Duplicate destinations')
    require(len({r['source'] for r in records}) == len(records), 'Duplicate sources')
    for row in records:
        relative = Path(row['destination'])
        require(not relative.is_absolute() and '..' not in relative.parts, 'Unsafe path')
        expected = 'migration/etapa2/' + row['role'] + '/' + row['source']
        require(row['destination'] == expected, 'Unexpected destination')
        raw = (ROOT / relative).read_bytes()
        require(len(raw) == row['bytes'], f'Size changed: {relative}')
        require(hashlib.sha256(raw).hexdigest() == row['sha256'], f'Bytes changed: {relative}')
        blob = hashlib.sha1(b'blob ' + str(len(raw)).encode() + b'\0' + raw).hexdigest()
        require(blob == row['blob'], f'Git object changed: {relative}')

    actual = {
        p.relative_to(ROOT).as_posix()
        for area in ['source', 'reference-only']
        for p in (ROOT / 'migration/etapa2' / area).rglob('*') if p.is_file()
    }
    require(actual == set(paths), 'Unlisted or missing extracted files')
    catalog_path = ROOT / 'migration/etapa2/source' / CATALOG
    require(hashlib.sha256(catalog_path.read_bytes()).hexdigest() ==
            '666d24b453f29a360d5bd42547a5c57ff2256b4430622714a05bfbdb21c3d718',
            'Curated catalog differs from the validated baseline')
    catalog = read_json(catalog_path)
    vocabulary = catalog['vocabulary']
    categories = catalog['categories']
    require(len(vocabulary) == 132 and len(categories) == 18, 'Catalog counts changed')
    require(len({v['vocab_id'] for v in vocabulary}) == 132, 'Duplicate VOC')
    category_ids = {c['categoria_id'] for c in categories}
    require(len(category_ids) == 18, 'Duplicate category')
    require(all(v['categoria_id'] in category_ids for v in vocabulary), 'Unknown category')
    approved = read_json(DOCS / 'PARES_VOC_ARASAAC_VERIFICADOS.json')
    require(len(approved) == 132, 'Approval matrix count changed')
    expected = {p['voc']: p for p in approved}
    require(len(expected) == 132, 'Duplicate approval VOC')
    for word in vocabulary:
        p = expected[word['vocab_id']]
        require((word['texto'], word['categoria_id'], word['arasaac_id'], word['estrategia_visual']) ==
                (p['word'], p['category'], p['arasaacId'], p['strategy']),
                f'Unapproved association: {word["vocab_id"]}')
    pictures = [v for v in vocabulary if v['estrategia_visual'] == 'PICTOGRAMA_VALIDADO']
    texts = [v for v in vocabulary if v['estrategia_visual'] == 'SOLO_TEXTO']
    require(len(pictures) == 119 and len(texts) == 13, '119/13 contract changed')
    require(len({v['arasaac_id'] for v in pictures}) == 116, 'Pictogram IDs changed')
    require(all(not v['arasaac_id'] for v in texts), 'Text-only word has an ID')
    require(all(not expected[v]['arasaacId'] for v in ['VOC_072', 'VOC_074', 'VOC_097']),
            'Estoy, Dame or Ruido has an ID')
    # Activation changes this stage's contract and requires the Stage 3 review.
    for path in [ROOT / 'settings.gradle.kts', ROOT / 'build.gradle.kts', ROOT / 'app/build.gradle.kts']:
        require('migration/' not in path.read_text(encoding='utf-8'), 'Staging activated in Gradle')
    require(not (ROOT / 'app/src/main/java/com/neuronovaapps/comunicago/.git').exists(),
            'Nested Git repository remains')
    return dict(result='ETAPA_2_VERIFICADA', files=len(records), vocabulary=132,
                categories=18, associations=119, uniquePictograms=116, textOnly=13,
                runtimeActivated=False, androidTestsRun=False)


if __name__ == '__main__':
    print(json.dumps(verify(), ensure_ascii=False, indent=2))
