export function timeAgo(dateString) {
    const now = new Date();
    const date = new Date(dateString);
    const secondsPast = (now.getTime() - date.getTime()) / 1000;
    
    if (secondsPast < 60) {
        const seconds = Math.floor(secondsPast);
        return `${seconds} second${seconds > 1 ? 's' : ''} ago`; // X秒前 → X second(s) ago
    } else if (secondsPast < 3600) {
        const minutes = Math.floor(secondsPast / 60);
        return `${minutes} minute${minutes > 1 ? 's' : ''} ago`; // X分钟前 → X minute(s) ago
    } else if (secondsPast <= 86400) {
        const hours = Math.floor(secondsPast / 3600);
        return `${hours} hour${hours > 1 ? 's' : ''} ago`; // X小时前 → X hour(s) ago
    } else {
        const daysPast = Math.floor(secondsPast / 86400);
        if (daysPast === 1) {
            return '1 day ago'; // 1天前 → 1 day ago
        } else {
            return `${daysPast} days ago`; // X天前 → X days ago
        }
    }
}