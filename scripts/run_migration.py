import os
import psycopg2
import argparse
from getpass import getpass

def run_migration():
    # Set up argument parser with defaults matching docker-compose.yml
    parser = argparse.ArgumentParser(description='Run database migrations for Leaderboard')
    parser.add_argument('--dbname', default='leaderboard', help='Database name (default: leaderboard)')
    parser.add_argument('--user', default='leaderboard', help='Database user (default: leaderboard)')
    parser.add_argument('--host', default='localhost', help='Database host (default: localhost)')
    parser.add_argument('--port', default='5432', help='Database port (default: 5432)')
    
    # Only prompt for password if not provided as an argument
    parser.add_argument('--password', default='leaderboard123', help='Database password (default: leaderboard123, or leave empty to be prompted)')
    
    args = parser.parse_args()
    
    # If password not provided as argument, prompt for it
    password = args.password if args.password else getpass('Enter database password: ')
    
    # Database connection parameters
    db_params = {
        'dbname': args.dbname,
        'user': args.user,
        'password': password,
        'host': args.host,
        'port': args.port
    }

    # SQL to create the user_score table if it doesn't exist
    create_table_sql = """
    CREATE TABLE IF NOT EXISTS user_score (
        id BIGSERIAL PRIMARY KEY,
        leaderboard_instance_id VARCHAR(255) NOT NULL,
        user_id VARCHAR(255) NOT NULL,
        score DOUBLE PRECISION NOT NULL,
        created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
        UNIQUE (leaderboard_instance_id, user_id)
    );
    """
    
    # SQL to create indexes
    create_indexes_sql = [
        """CREATE INDEX IF NOT EXISTS idx_user_score_instance_user 
           ON user_score(leaderboard_instance_id, user_id);""",
        """CREATE INDEX IF NOT EXISTS idx_user_score_instance_score 
           ON user_score(leaderboard_instance_id, score);"""
    ]

    conn = None
    cursor = None
    try:
        # Connect to PostgreSQL
        print("📡 Connecting to the PostgreSQL database...")
        conn = psycopg2.connect(**db_params)
        conn.autocommit = True
        cursor = conn.cursor()

        # Create table
        print("🔄 Creating user_score table...")
        cursor.execute(create_table_sql)
        
        # Create indexes
        print("🔨 Creating indexes...")
        for index_sql in create_indexes_sql:
            cursor.execute(index_sql)
        
        print("✅ Database migration completed successfully!")
        
    except psycopg2.Error as e:
        print(f"❌ Database error: {e.pgerror if e.pgerror else e}")
    except Exception as e:
        print(f"❌ An error occurred: {e}")
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()
            print("🔌 Database connection closed.")

if __name__ == "__main__":
    run_migration()
