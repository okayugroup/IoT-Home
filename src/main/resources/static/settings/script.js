
const canvas = document.getElementById('canvas')
// デバイスのピクセル比率を取得
var devicePixelRatio = window.devicePixelRatio || 1;

// CanvasのCSSサイズを取得
var rect = canvas.getBoundingClientRect();

// Canvasの幅と高さをデバイスのピクセル比率に基づいて設定
canvas.width = rect.width * devicePixelRatio;
canvas.height = rect.height * devicePixelRatio;

const ellipsis = '...'


window.onload = function() {
    const ctx = canvas.getContext('2d');
    ctx.fillStyle = 'black'
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    ctx.font = '15px "Noto Sans JP"';
    function truncateText(text, maxWidth) {
        var width = ctx.measureText(text).width;
        console.log([maxWidth, width])
        if (width <= maxWidth) {
            return text;
        }
        for (var i = text.length - 1; i > 0; i--) {
            var truncated = text.substring(0, i) + ellipsis;
            width = ctx.measureText(truncated).width;
            if (width <= maxWidth) {
                return truncated;
            }
        }
        return ellipsis;  // 最小でも省略記号を返す
    }

    function fillText(text, x, y, max) {
        ctx.fillStyle = 'black'
        const text2 = truncateText(text, max)
        ctx.fillText(text2, x, y)
    }

    ctx.fillStyle = 'black';
    ctx.fillRect(0, 0, canvas.width, canvas.height);
    ctx.beginPath();
    ctx.moveTo(20, 20);
    ctx.lineTo(280, 20);
    ctx.quadraticCurveTo(300, 20, 300, 40);
    ctx.lineTo(300, 180);
    ctx.quadraticCurveTo(300, 200, 280, 200);
    ctx.lineTo(20, 200);
    ctx.quadraticCurveTo(0, 200, 0, 180);
    ctx.lineTo(0, 40);
    ctx.quadraticCurveTo(0, 20, 20, 20);
    ctx.fillStyle = 'white';
    ctx.fill();


    ctx.fillStyle = 'gray'
    ctx.beginPath();
    ctx.moveTo(180, 20);
    ctx.lineTo(280, 20);
    ctx.quadraticCurveTo(300, 20, 300, 40)
    ctx.lineTo(300, 50);
    ctx.lineTo(180, 50)
    ctx.fill();
    fillText("Windows Pow+ershell コマンド", 245, 35, 105);

    ctx.fillStyle = 'darkgray'
    ctx.beginPath();
    ctx.moveTo(60, 20);
    ctx.lineTo(180, 20);
    ctx.lineTo(190, 35);
    ctx.lineTo(180, 50);
    ctx.lineTo(60, 50)
    ctx.fill();
    fillText("コマンド実行", 125, 35, 110);

    ctx.fillStyle = 'lightblue'
    ctx.beginPath()
    ctx.moveTo(0, 40);
    ctx.quadraticCurveTo(0, 20, 20, 20);
    ctx.lineTo(60, 20);
    ctx.lineTo(70, 35);
    ctx.lineTo(60, 50);
    ctx.lineTo(0, 50)
    ctx.fill()
    fillText("入力", 30, 35, 50)
}