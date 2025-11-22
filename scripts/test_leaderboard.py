#!/usr/bin/env python3

import os
import time
import subprocess
import psycopg2
import requests
import json
import time
from typing import Dict, Any, Optional, List, Tuple
from urllib.parse import urljoin

# Configuration
BASE_URL = "http://localhost:8080/api/v1/leaderboard"
HEALTH_CHECK_URL = "http://localhost:8080/actuator/health"
DB_CONFIG = {
    "host": "localhost",
    "database": "leaderboard",
    "user": "leaderboard",
    "password": "leaderboard123",
    "port": 5432
}
REDIS_PASSWORD = "leaderboard123"

def run_command(cmd: List[str], cwd: str = None) -> subprocess.CompletedProcess:
    """Run a shell command and return the result."""
    print(f"🚀 Running: {' '.join(cmd)}")
    return subprocess.run(cmd, cwd=cwd, check=True, text=True, capture_output=True)

def wait_for_service(host: str, port: int, timeout: int = 30) -> bool:
    """Wait for a service to become available."""
    import socket
    start_time = time.time()
    print(f"⏳ Waiting for {host}:{port}...")
    
    while time.time() - start_time < timeout:
        try:
            with socket.create_connection((host, port), timeout=1):
                print(f"✅ {host}:{port} is available")
                return True
        except (socket.timeout, ConnectionRefusedError):
            print(".", end="", flush=True)
            time.sleep(1)
    
    print(f"\n❌ {host}:{port} not available after {timeout} seconds")
    return False

def post_to_api(endpoint: str, data: Dict[str, Any]) -> Dict[str, Any]:
    """Helper function to make POST requests to the API."""
    url = urljoin(BASE_URL, endpoint)
    headers = {'Content-Type': 'application/json'}
    try:
        response = requests.post(url, json=data, headers=headers)
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        print(f"❌ Error calling {endpoint}: {e}")
        if hasattr(e, 'response') and e.response is not None:
            print(f"Status code: {e.response.status_code}")
            print(f"Response: {e.response.text}")
        raise

def clear_data_stores():
    """Clear data from both PostgreSQL and Redis."""
    print("\n🧹 Clearing data stores...")
    
    # Clear PostgreSQL data
    try:
        conn = psycopg2.connect(**DB_CONFIG)
        cursor = conn.cursor()
        cursor.execute("TRUNCATE TABLE user_score CASCADE")
        conn.commit()
        print("✅ PostgreSQL data cleared")
    except Exception as e:
        print(f"❌ Error clearing PostgreSQL: {e}")
    finally:
        if 'conn' in locals():
            cursor.close()
            conn.close()
    
    # Clear Redis data
    try:
        import redis
        r = redis.Redis(host='localhost', port=6379, db=0, 
                        password=REDIS_PASSWORD, decode_responses=True)
        # Delete all keys matching the leaderboard pattern
        keys = r.keys('leaderboard:*')
        if keys:
            r.delete(*keys)
        print("✅ Redis data cleared")
    except Exception as e:
        print(f"❌ Error clearing Redis: {e}")

def setup_test_data():
    """Set up test data using the write API."""
    print("\n📝 Setting up test data using write API...")
    
    # Insert test data (10 users with scores from 1000 to 100, decreasing by 100)
    for i in range(1, 11):
        user_id = f'user{i}'
        score = 1000 - ((i-1) * 100)
        
        request_data = {
            "leaderboardInstanceId": "leaderboard1",
            "userId": user_id,
            "score": float(score)
        }
        
        print(f"  - Setting score for {user_id}: {score}")
        response = post_to_api("leaderboard/write/score", request_data)
        
        if not response.get('success', False):
            print(f"❌ Failed to set score for {user_id}")
    
    print("\n✅ Test data inserted via write API")

def test_endpoint(name: str, endpoint: str, params: Optional[Dict[str, Any]] = None):
    """Test an API endpoint and print the results."""
    url = urljoin(BASE_URL, endpoint)
    print(f"\n🔍 Testing {name}:")
    print(f"URL: {url}")
    print(f"Params: {params or 'None'}")
    
    try:
        response = requests.get(url, params=params)
        response.raise_for_status()
        print("✅ Success:")
        print(json.dumps(response.json(), indent=2))
    except requests.exceptions.RequestException as e:
        print(f"❌ Error: {e}")
        if hasattr(e, 'response') and e.response is not None:
            print(f"Status code: {e.response.status_code}")
            print(f"Response: {e.response.text}")

def main():
    try:
        # Start infrastructure
        print("🚀 Starting infrastructure with Docker Compose...")
        run_command(["docker-compose", "up", "-d"])
        
        # Wait for services
        if not wait_for_service("localhost", 5432) or not wait_for_service("localhost", 6379):
            print("❌ Failed to start services")
            return 1
        
#         # Wait for the application to start and be ready to accept requests
#         print("\n⏳ Waiting for the application to start...")
#         max_attempts = 10
#         for attempt in range(1, max_attempts + 1):
#             try:
#                 health_url = HEALTH_CHECK_URL
#                 response = requests.get(health_url)
#                 if response.status_code == 200 and response.json().get('status') == 'UP':
#                     print("✅ Application is up and running")
#                     break
#             except requests.exceptions.RequestException:
#                 pass
#
#             if attempt < max_attempts:
#                 print(f"  Attempt {attempt}/{max_attempts}: Application not ready, retrying in 3 seconds...")
#                 time.sleep(3)
#         else:
#             print("❌ Application did not start in time")
#             return 1
#
#         # Clear data stores and set up test data
        clear_data_stores()
        setup_test_data()
        
        # Test leaderboard endpoints
        print("\n🏆 Testing Leaderboard Endpoints")
        
        # Test top scores endpoint
        test_endpoint(
            "Top Scores",
            "leaderboard/top",
            {"instanceId": "leaderboard1", "limit": 5}
        )
        
        # Test user rank endpoint
        test_endpoint(
            "User Rank",
            "leaderboard/rank/user5",
            {"instanceId": "leaderboard1"}
        )
        
        # Test top scores with default limit (should be 10)
        test_endpoint(
            "Top Scores with Default Limit",
            "leaderboard/top",
            {"instanceId": "leaderboard1"}
        )
        
        print("\n✅ All tests completed successfully!")
        
    except subprocess.CalledProcessError as e:
        print(f"❌ Command failed with exit code {e.returncode}:")
        print(f"STDOUT: {e.stdout}")
        print(f"STDERR: {e.stderr}")
        return 1
    except Exception as e:
        print(f"❌ An error occurred: {e}")
        return 1
    finally:
        print("\nℹ️  To stop the infrastructure, run: docker-compose down")
        # Uncomment the following line to automatically stop containers after tests
        # run_command(["docker-compose", "down"])
    
    return 0

if __name__ == "__main__":
    exit(main())
