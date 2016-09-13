#####################################################################################################
#
# Copyright (c) Microsoft Corporation. All rights reserved.
#
# PowerShell for Azure KeyVault
#
# Tutorial From: https://azure.microsoft.com/en-us/documentation/articles/key-vault-get-started/
#
#####################################################################################################

<# 
.SYNOPSIS 
Locates the Microsoft Azure PowerShell cmdlets version currently installed. You want 1.0.1 or higher.
 
.DESCRIPTION 
Function to retrieve the Microsoft Azure PowerShell cmdlets version. 
 
.EXAMPLE 
PS> .\Get-AzurePowerShellCmdletsVersion.ps1 
#> 
 
$name='Azure' 
 
if(Get-Module -ListAvailable |  
    Where-Object { $_.name -eq $name })  
{  
    (Get-Module -ListAvailable | Where-Object{ $_.Name -eq $name }) |  
    Select Version, Name, Author, PowerShellVersion  | Format-List;  
}  
else  
{  
    “The Azure PowerShell module is not installed.” 
}

# Sample Get Help
#Get-Help Login-AzureRmAccount -Detailed

# Sign in to Azure
Login-AzureRmAccount

# Check Azure Subscription
Get-AzureRmSubscription

# Set the Azure Subscriotion to use
Set-AzureRmContext -SubscriptionId <SUBSCRIPTION_ID>

# We will create a new resource group named ContosoResourceGroup
#New-AzureRmResourceGroup –Name 'ContosoResourceGroup' –Location 'East Asia'

# Get aexisting resource group in subscription
# Get-AzureRmResourceGroup

# Create new KeyVault
New-AzureRmKeyVault -VaultName 'etellerkeyvault' -ResourceGroupName 'eteller' -Location 'East US 2'

# Create Password Variable
$secretvalue = ConvertTo-SecureString '<password>' -AsPlainText -Force
$secret = Set-AzureKeyVaultSecret -VaultName 'etellerkeyvault' -Name 'etellerwebapppassword' -SecretValue $secretvalue

# Get URI for the created secred
$secret.Id

# To view your secret
Get-AzureKeyVaultSecret –VaultName 'etellerkeyvault'

# Follow step from top tutorial to register your application with Azure AD (Must be same AD that your Subscription and KeyVault are under)
$clientID = '<CLINET_ID>'

# Authorize that same application to read secrets in your vault
Set-AzureRmKeyVaultAccessPolicy -VaultName 'etellerkeyvault' -ServicePrincipalName $clientID -PermissionsToSecrets Get

# List all the keys in the key vault
$Keys = Get-AzureKeyVaultKey -VaultName 'etellerkeyvault'
Get-AzureKeyVaultKey -VaultName 'etellerkeyvault'
$Keys[0]

Get-AzureKeyVaultKey -VaultName 'etellerkeyvault' -KeyName 'CMK_Auto1'