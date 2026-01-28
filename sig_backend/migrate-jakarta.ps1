# Phase 2 Migration Script - javax to jakarta
# Run this from sig_backend directory

$srcPath = "src/main/java"

Write-Host "=== SIG Backend Migration: javax -> jakarta ===" -ForegroundColor Cyan
Write-Host ""

# Define replacement patterns
$replacements = @{
    "import javax.persistence." = "import jakarta.persistence."
    "import javax.validation." = "import jakarta.validation."
    "import javax.servlet." = "import jakarta.servlet."
    "import javax.transaction." = "import jakarta.transaction."
    "import javax.mail." = "import jakarta.mail."
    "import javax.annotation." = "import jakarta.annotation."
    "import javax.xml." = "import jakarta.xml."
}

# Get all Java files
$javaFiles = Get-ChildItem -Path $srcPath -Filter "*.java" -Recurse

$filesModified = 0
$totalReplacements = 0

foreach ($file in $javaFiles) {
    $content = Get-Content -Path $file.FullName -Raw
    $originalContent = $content
    $fileReplacements = 0
    
    foreach ($old in $replacements.Keys) {
        $new = $replacements[$old]
        if ($content -match [regex]::Escape($old)) {
            $count = ([regex]::Matches($content, [regex]::Escape($old))).Count
            $content = $content -replace [regex]::Escape($old), $new
            $fileReplacements += $count
        }
    }
    
    if ($content -ne $originalContent) {
        Set-Content -Path $file.FullName -Value $content -NoNewline
        $filesModified++
        $totalReplacements += $fileReplacements
        Write-Host "  [UPDATED] $($file.FullName) ($fileReplacements replacements)" -ForegroundColor Green
    }
}

Write-Host ""
Write-Host "=== Migration Summary ===" -ForegroundColor Cyan
Write-Host "  Files modified: $filesModified"
Write-Host "  Total replacements: $totalReplacements"
Write-Host ""
Write-Host "Done! Please run './gradlew build' to verify." -ForegroundColor Yellow
