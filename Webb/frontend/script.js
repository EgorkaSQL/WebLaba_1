const canvas = document.getElementById('coordinate-plane');
const ctx = canvas.getContext('2d');
const canvasSize = 400;
const halfCanvasSize = canvasSize / 2;
const pixelRatio = canvasSize / 6;

function drawStaticAreas()
{
    ctx.fillStyle = 'rgba(0, 0, 255, 0.5)';

    // Четверть окружности
    ctx.beginPath();
    ctx.arc(halfCanvasSize, halfCanvasSize, 1 * pixelRatio, -Math.PI / 2, 0, false);
    ctx.lineTo(halfCanvasSize, halfCanvasSize);
    ctx.closePath();
    ctx.fill();

    // Треугольник
    ctx.beginPath();
    ctx.moveTo(halfCanvasSize, halfCanvasSize);
    ctx.lineTo(halfCanvasSize - 1 * pixelRatio, halfCanvasSize);
    ctx.lineTo(halfCanvasSize, halfCanvasSize - 2 * pixelRatio);
    ctx.closePath();
    ctx.fill();

    // Прямоугольник
    ctx.fillRect(halfCanvasSize - 1 * pixelRatio, halfCanvasSize, 1 * pixelRatio, 2 * pixelRatio);
}

function drawAxes()
{
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    ctx.beginPath();
    ctx.moveTo(0, halfCanvasSize);
    ctx.lineTo(canvasSize, halfCanvasSize);  // Ось X
    ctx.moveTo(halfCanvasSize, 0);
    ctx.lineTo(halfCanvasSize, canvasSize);  // Ось Y
    ctx.strokeStyle = 'black';
    ctx.stroke();
    ctx.fillStyle = 'black';
    ctx.font = '12px Arial';
    ctx.fillText('x', canvasSize - 10, halfCanvasSize + 15);
    ctx.fillText('y', halfCanvasSize - 10, 10);
    const labels = ['-R', '-R/2', 'R/2', 'R'];
    const positions = [-2, -1, 1, 2];
    positions.forEach((pos, i) => {
        ctx.fillText(labels[i], halfCanvasSize + pos * pixelRatio - 10, halfCanvasSize + 15);
        ctx.moveTo(halfCanvasSize + pos * pixelRatio, halfCanvasSize - 5);
        ctx.lineTo(halfCanvasSize + pos * pixelRatio, halfCanvasSize + 5);
    });
    positions.forEach((pos, i) => {
        ctx.fillText(labels[i], halfCanvasSize - 25, halfCanvasSize - pos * pixelRatio + 5);
        ctx.moveTo(halfCanvasSize - 5, halfCanvasSize - pos * pixelRatio);
        ctx.lineTo(halfCanvasSize + 5, halfCanvasSize - pos * pixelRatio);
    });
    ctx.stroke();
}

function plotPoint(x, y, r)
{
    const scaledX = (x / r) * 2;
    const scaledY = (y / r) * 2;
    const canvasX = halfCanvasSize + scaledX * pixelRatio;
    const canvasY = halfCanvasSize - scaledY * pixelRatio;
    ctx.beginPath();
    ctx.arc(canvasX, canvasY, 5, 0, 2 * Math.PI);
    ctx.fillStyle = 'red';
    ctx.fill();
}

window.onload = function ()
{
    drawAxes();
    drawStaticAreas();
};

document.getElementById('submit-button').addEventListener('click', function (event)
{
    event.preventDefault();
    const r = parseFloat(document.getElementById('radius').value);
    const y = parseFloat(document.getElementById('y-coord').value);

    const xInput = document.querySelector('input[name="x-coord"]:checked');
    if (!xInput)
    {
        alert("Выберите значение X!");
        return;
    }
    const x = parseFloat(xInput.value);

    if (isNaN(r) || isNaN(x) || isNaN(y))
    {
        alert("Все значения должны быть числами!");
        return;
    }
    if (x < -3 || x > 5)
    {
        alert("Значение X должно быть в диапазоне от -3 до 5.");
        return;
    }
    if (y < -3 || y > 5)
    {
        alert("Значение Y должно быть в диапазоне от -3 до 5.");
        return;
    }

    fetch('/api/', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ r, x, y })
    })
    .then(response => response.json())
    .then(data => {
        const resultRow = document.createElement('tr');
        resultRow.innerHTML = `
            <td>${r}</td>
            <td>${x}</td>
            <td>${y}</td>
            <td>${data.isInArea ? 'Да' : 'Нет'}</td>
            <td>${data.currentTime}</td>
            <td>${data.execTime} ms</td>
        `;
        document.querySelector('#results tbody').appendChild(resultRow);
        plotPoint(x, y, r);
    })
    .catch(error => console.error('Ошибка:', error));
});
