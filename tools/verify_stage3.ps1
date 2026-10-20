$ErrorActionPreference = 'Stop'
Set-Location (Split-Path $PSScriptRoot -Parent)
$manifest = Get-Content docs/migration/EXTRACCION_ETAPA_2.json -Raw | ConvertFrom-Json
foreach ($entry in $manifest.files) {
    $hash = (Get-FileHash -LiteralPath $entry.destination -Algorithm SHA256).Hash.ToLowerInvariant()
    if ($hash -ne $entry.sha256) { throw "Stage 2 source changed: $($entry.destination)" }
}
$catalogPath = 'app/src/main/assets/communication/communication_catalog_v1.json'
$catalogHash = (Get-FileHash -LiteralPath $catalogPath -Algorithm SHA256).Hash.ToLowerInvariant()
if ($catalogHash -ne '666d24b453f29a360d5bd42547a5c57ff2256b4430622714a05bfbdb21c3d718') { throw 'Curated catalog changed' }
$catalog = Get-Content -LiteralPath $catalogPath -Raw | ConvertFrom-Json
if ($catalog.vocabulary.Count -ne 132 -or $catalog.categories.Count -ne 18) { throw 'Catalog counts changed' }
$associations = @($catalog.vocabulary | Where-Object { -not [string]::IsNullOrWhiteSpace($_.arasaac_id) }).Count
$textOnly = @($catalog.vocabulary | Where-Object { $_.estrategia_visual -eq 'SOLO_TEXTO' }).Count
if ($associations -ne 119 -or $textOnly -ne 13) { throw 'Curatorial counts changed' }
$pairs = Get-Content docs/migration/PARES_VOC_ARASAAC_VERIFICADOS.json -Raw | ConvertFrom-Json
foreach ($word in $catalog.vocabulary) {
    $pair = @($pairs | Where-Object { $_.voc -eq $word.vocab_id })
    if ($pair.Count -ne 1 -or $pair[0].word -cne $word.texto -or $pair[0].category -cne $word.categoria_id -or [string]$pair[0].arasaacId -cne [string]$word.arasaac_id -or $pair[0].strategy -cne $word.estrategia_visual) {
        throw "Curatorial pair changed: $($word.vocab_id)"
    }
}
$forbidden = Get-ChildItem app/src/main -Recurse -Filter '*.kt' | Select-String -Pattern 'com\.neuronovaapps\.rutinago|RutinaGoDatabase|PictogramReferenceRepository|domain\.user|domain\.catalog|androidx\.room'
if ($forbidden) { throw "Host dependency: $forbidden" }
$build = Get-Content app/build.gradle.kts -Raw
if ($build -match 'sourceSets|migration/etapa2') { throw 'Staging activated as a source set' }
[ordered]@{
    preservedStage2Files = $manifest.files.Count
    catalogSha256 = $catalogHash
    vocabulary = $catalog.vocabulary.Count
    categories = $catalog.categories.Count
    pictogramAssociations = $associations
    textOnly = $textOnly
    verifiedCuratorialPairs = $pairs.Count
    hostDependencies = 0
} | ConvertTo-Json
