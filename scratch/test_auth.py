import json
import time
import base64
import urllib.request
import urllib.error
from cryptography.hazmat.primitives import serialization
from cryptography.hazmat.primitives.asymmetric import padding
from cryptography.hazmat.primitives import hashes

def base64url(data):
    if isinstance(data, str):
        data = data.encode('utf-8')
    return base64.urlsafe_b64encode(data).rstrip(b'=').decode('ascii')

with open('credentials/google-credentials.json', 'r') as f:
    creds = json.load(f)

header = base64url(json.dumps({"alg": "RS256", "typ": "JWT"}))
now = int(time.time())
payload = base64url(json.dumps({
    "iss": creds["client_email"],
    "sub": creds["client_email"],
    "aud": "https://oauth2.googleapis.com/token",
    "iat": now,
    "exp": now + 3600,
    "scope": "https://www.googleapis.com/auth/spreadsheets"
}))

signing_input = f"{header}.{payload}".encode('ascii')

private_key = serialization.load_pem_private_key(
    creds["private_key"].encode('utf-8'),
    password=None
)

signature = private_key.sign(
    signing_input,
    padding.PKCS1v15(),
    hashes.SHA256()
)

jwt = f"{header}.{payload}.{base64url(signature)}"

body = f"grant_type=urn%3Aietf%3Aparams%3Aoauth%3Agrant-type%3Ajwt-bearer&assertion={jwt}".encode('ascii')

req = urllib.request.Request(
    "https://oauth2.googleapis.com/token",
    data=body,
    headers={"Content-Type": "application/x-www-form-urlencoded"}
)

try:
    with urllib.request.urlopen(req) as resp:
        token_data = json.loads(resp.read().decode('utf-8'))
        print("TOKEN SUCCESS!", token_data.get("access_token")[:20] + "...")
        
        # Test calling Sheets API
        sheet_req = urllib.request.Request(
            "https://sheets.googleapis.com/v4/spreadsheets/14ZgPqEA8ICDsGJ3JjzY1gtcAaaldiJDM50t8ECQCeEM/values/Members!A2:F",
            headers={"Authorization": f"Bearer {token_data.get('access_token')}"}
        )
        try:
            with urllib.request.urlopen(sheet_req) as s_resp:
                print("SHEET SUCCESS!", s_resp.read().decode('utf-8')[:200])
        except urllib.error.HTTPError as se:
            print("SHEET FAILED:", se.code, se.read().decode('utf-8'))
            
except urllib.error.HTTPError as e:
    print("TOKEN FAILED:", e.code, e.read().decode('utf-8'))
