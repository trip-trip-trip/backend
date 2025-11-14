// sw.js
self.addEventListener('push', function(event) {
    console.log('Push 이벤트 수신:', event);

    let data = {
        title: '알림',
        body: '새로운 메시지가 있습니다',
        icon: '/icon.png',
        tag: 'default'
    };

    if (event.data) {
        try {
            data = event.data.json();
        } catch (e) {
            console.error('Push data 파싱 실패:', e);
        }
    }

    const options = {
        body: data.body,
        icon: data.icon || '/icon.png',
        badge: '/badge.png',
        tag: data.tag || 'notification',
        requireInteraction: false,
        data: data
    };

    event.waitUntil(
        self.registration.showNotification(data.title, options)
    );
});

self.addEventListener('notificationclick', function(event) {
    console.log('알림 클릭됨:', event);
    event.notification.close();

    event.waitUntil(
        clients.openWindow('/')
    );
});