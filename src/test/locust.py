from locust import HttpUser, task, between
import random
from datetime import datetime, timedelta
import time

class MeetingCreateAndSearchUser(HttpUser):
    wait_time = between(1, 2)  # 1초에서 2초 사이 대기 시간
    auth_token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI0IiwiZW1haWwiOiJzdW5nanUwOTA5QGdtYWlsLmNvbSIsInVzZXJSb2xlIjoiVVNFUiIsImV4cCI6MTczMjA4ODA2MiwiaWF0IjoxNzMyMDg0NDYyfQ.OD4OawKEbANZnMhoYU_GeyXjpv8uY1JuQy9zzoIObmw"
    # 인증 헤더 추가
    headers = {
        "Authorization": f"Bearer {auth_token}"
    }

    # 더미 데이터 10만 개 생성 후 조회 (10만 개 데이터를 모두 생성 후 조회를 시작)
    @task  # 이 작업을 10배 더 자주 실행하도록 우선순위 설정
    def search_meeting_by_title_v1(self):
        # 랜덤으로 제목을 생성하여 검색 (예: "서울 삼성동 급벙")
        title = "즐거운"  # 위에서 생성한 제목을 사용하여 검색

        # GET 요청을 통해 제목으로 미팅 검색 (v1/search 엔드포인트 사용)
        response = self.client.get("/v1/search", params={"title": title, "page": 1, "size": 10}, headers=self.headers)

        # 요청이 성공했는지 확인
        if response.status_code == 200:
            print(f"v1/search 검색 성공: {title}")
        else:
            print(f"v1/search 검색 실패: {response.status_code} - {response.text}")

    # 더미 데이터 10만 개 생성 후 조회 (v2/search)
    @task # v2/search도 마찬가지로 높은 우선순위
    def search_meeting_by_title_v2(self):
        # 랜덤으로 제목을 생성하여 검색 (예: "서울 삼성동 급벙")
        title = "즐거운"  # 위에서 생성한 제목을 사용하여 검색

        # GET 요청을 통해 제목으로 미팅 검색 (v2/search 엔드포인트 사용)
        response = self.client.get("/v2/search", params={"title": title, "page": 1, "size": 10}, headers=self.headers)

        # 요청이 성공했는지 확인
        if response.status_code == 200:
            print(f"v2/search 검색 성공: {title}")
        else:
            print(f"v2/search 검색 실패: {response.status_code} - {response.text}")