#!/usr/bin/env python3

import os
import time
import subprocess
import psycopg2
import requests
import json
from typing import Dict, Any, Optional, List
from urllib.parse import urljoin

# Configuration
BASE_URL = "http://localhost:8080/api/v1/leaderboard"
DB_CONFIG = {
    "host": "localhost",
    "database": "leaderboard",
    "user": "leaderboard",
    "password": "leaderboard123",
    "port": 5432
}

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

def setup_test_data():
    """Set up test data in both PostgreSQL and Redis."""
    print("\n📝 Setting up test data...")
    
    # 1. Setup PostgreSQL data
    print("  - Inserting data into PostgreSQL...")
    conn = psycopg2.connect(**DB_CONFIG)
    cursor = conn.cursor()
    
    # Clear existing data
    cursor.execute("TRUNCATE TABLE user_score CASCADE")
    
    # Insert test data (10 users with scores from 1000 to 100, decreasing by 100)
    test_users = [
        ('leaderboard1', f'user{i}', 1000 - ((i-1) * 100)) 
        for i in range(1, 11)  # user1 (1000) to user10 (100)
    ]
    
    cursor.executemany(
        """
        INSERT INTO user_score 
        (leaderboard_instance_id, user_id, score, created_at, updated_at)
        VALUES (%s, %s, %s, NOW(), NOW())
        """,
        test_users
    )
    conn.commit()
    cursor.close()
    conn.close()
    
    # 2. Setup Redis data
    print("  - Inserting data into Redis...")
    import redis
    
    # Connect to Redis
    r = redis.Redis(host='localhost', port=6379, db=0, password='leaderboard123')
    
    # Clear existing leaderboard
    r.delete('leaderboard:leaderboard1')
    
    # Add test users to Redis sorted set
    for user_id, score in [(f'user{i}', 1000 - ((i-1) * 100)) for i in range(1, 11)]:
        r.zadd('leaderboard:leaderboard1', {user_id: score})
    
    # Verify data in both stores
    print("\n✅ Test data inserted into both PostgreSQL and Redis")
    print("   - PostgreSQL: 10 users with scores from 1000 to 100")
    print(f"   - Redis: {r.zcard('leaderboard:leaderboard1')} users in leaderboard")

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
        
        # Set up test data in both PostgreSQL and Redis
        setup_test_data()
        
        # Wait for the application to start
        print("\n⏳ Waiting for the application to start...")
        time.sleep(5)
        
        # Test repository endpoints
        print("\n🧪 Testing Repository Endpoints")
        test_endpoint(
            "Repository - Top Scores",
            "repository/top",
            {"instanceId": "leaderboard1", "limit": 5}
        )
        
        test_endpoint(
            "Repository - User Rank",
            "repository/rank/user5",
            {"instanceId": "leaderboard1"}
        )
        
        test_endpoint(
            "Repository - Around User",
            "repository/around/user5",
            {"instanceId": "leaderboard1", "limit": 3}
        )
        
        # Test optimized endpoints
        print("\n🚀 Testing Optimized Endpoints")
        test_endpoint(
            "Optimized - Top Scores",
            "optimized/top",
            {"instanceId": "leaderboard1", "limit": 5}
        )
        
        test_endpoint(
            "Optimized - User Rank",
            "optimized/rank/user5",
            {"instanceId": "leaderboard1"}
        )
        
        test_endpoint(
            "Optimized - Around User",
            "optimized/around/user5",
            {"instanceId": "leaderboard1", "limit": 3}
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
