$databaseUsername = Read-Host 'MySQL username (press Enter for root)'
if ([string]::IsNullOrWhiteSpace($databaseUsername)) {
    $databaseUsername = 'root'
}

$adminName = Read-Host 'Admin full name (press Enter for Campus Administrator)'
if ([string]::IsNullOrWhiteSpace($adminName)) {
    $adminName = 'Campus Administrator'
}

$adminEmail = Read-Host 'Admin email'
if ([string]::IsNullOrWhiteSpace($adminEmail)) {
    throw 'Admin email is required.'
}

$databaseSecurePassword = Read-Host 'MySQL password' -AsSecureString
$adminSecurePassword = Read-Host 'New admin password (minimum 12 characters)' -AsSecureString
$databasePointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($databaseSecurePassword)
$adminPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($adminSecurePassword)

try {
    $databasePassword = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($databasePointer)
    $adminPassword = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($adminPointer)

    if ($adminPassword.Length -lt 12) {
        throw 'Admin password must contain at least 12 characters.'
    }

    $env:DB_USERNAME = $databaseUsername
    $env:DB_PASSWORD = $databasePassword
    $env:ADMIN_NAME = $adminName
    $env:ADMIN_EMAIL = $adminEmail
    $env:ADMIN_PASSWORD = $adminPassword

    Write-Host 'Starting CampusCare. The admin will be created during startup...' -ForegroundColor Cyan
    & "$PSScriptRoot\mvnw.cmd" spring-boot:run
}
finally {
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($databasePointer)
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($adminPointer)
    Remove-Item Env:DB_PASSWORD -ErrorAction SilentlyContinue
    Remove-Item Env:ADMIN_PASSWORD -ErrorAction SilentlyContinue
    Remove-Item Env:ADMIN_EMAIL -ErrorAction SilentlyContinue
    Remove-Item Env:ADMIN_NAME -ErrorAction SilentlyContinue
}
