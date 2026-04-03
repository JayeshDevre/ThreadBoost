// Tabs Logic
document.querySelectorAll('.tab-btn').forEach(btn => {
    btn.addEventListener('click', () => {
        document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
        document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
        
        btn.classList.add('active');
        document.getElementById(btn.dataset.tab).classList.add('active');
    });
});

// Race Logic Engine
function runRace(startBtnId, endpoint, isBlocking, typePrefix, expectedDurationMs) {
    const startBtn = document.getElementById(startBtnId);
    
    // UI Elements
    const blockingTimer = document.getElementById(`${typePrefix}-blocking-timer`);
    const asyncTimer = document.getElementById(`${typePrefix}-async-timer`);
    const blockingProgress = document.getElementById(`${typePrefix}-blocking-progress`);
    const asyncProgress = document.getElementById(`${typePrefix}-async-progress`);

    startBtn.disabled = true;
    startBtn.innerText = 'Racing...';

    blockingProgress.style.width = '0%';
    asyncProgress.style.width = '0%';

    const startTime = Date.now();
    let blockingDone = false;
    let asyncDone = false;

    // Fake Animation
    const timerInterval = setInterval(() => {
        const elapsed = Date.now() - startTime;

        if (!blockingDone) {
            blockingTimer.innerText = (elapsed / 1000).toFixed(2) + 's';
            blockingProgress.style.width = Math.min(95, (elapsed / expectedDurationMs.blocking) * 100) + '%';
        }

        if (!asyncDone) {
            asyncTimer.innerText = (elapsed / 1000).toFixed(2) + 's';
            asyncProgress.style.width = Math.min(95, (elapsed / expectedDurationMs.async) * 100) + '%';
        }

        if (blockingDone && asyncDone) {
            clearInterval(timerInterval);
            startBtn.disabled = false;
            startBtn.innerText = 'Start Race 🏁';
        }
    }, 50);

    // API Calls
    fetch(`/blocking/${endpoint}`).then(() => {
        blockingDone = true;
        blockingProgress.style.width = '100%';
        blockingTimer.innerText = ((Date.now() - startTime) / 1000).toFixed(2) + 's';
    }).catch(console.error);

    fetch(`/nonblocking/${endpoint}`).then(() => {
        asyncDone = true;
        asyncProgress.style.width = '100%';
        asyncTimer.innerText = ((Date.now() - startTime) / 1000).toFixed(2) + 's';
    }).catch(console.error);
}

// Attach Racers
document.getElementById('startIoRaceBtn').addEventListener('click', () => {
    runRace('startIoRaceBtn', 'dashboard', true, 'io', { blocking: 6000, async: 2000 });
});

document.getElementById('startCpuRaceBtn').addEventListener('click', () => {
    // CPU depends on machine speed, guessing ~2s blocking and 0.5s async
    runRace('startCpuRaceBtn', 'cpu-heavy?count=100', true, 'cpu', { blocking: 2000, async: 500 });
});

// Thread Starvation Grid Logic
function initGrid(size) {
    const grid = document.getElementById('execution-grid');
    grid.innerHTML = '';
    for(let i=0; i<size; i++) {
        const dot = document.createElement('div');
        dot.className = 'dot';
        dot.id = `dot-${i}`;
        grid.appendChild(dot);
    }
}
initGrid(200); // Initial draw

async function triggerLoadTest(type, btnObj, otherBtnId, count = 200) {
    btnObj.disabled = true;
    document.getElementById(otherBtnId).disabled = true;
    const oldText = btnObj.innerText;
    btnObj.innerText = 'Bombarding Server...';

    // Clear Grid
    initGrid(count);
    
    // Animate yellow pulsing to show they are waiting
    for(let i=0; i<count; i++) {
        document.getElementById(`dot-${i}`).classList.add('processing');
    }

    try {
        const resp = await fetch(`/trigger-load-test?type=${type}&count=${count}`);
        const textArr = await resp.json(); // ["SUCCESS", "FAIL", "SUCCESS"...]
        
        for(let i=0; i<count; i++) {
            const dot = document.getElementById(`dot-${i}`);
            dot.classList.remove('processing');
            if(textArr[i] === 'SUCCESS') {
                dot.classList.add('success');
            } else {
                dot.classList.add('fail');
            }
        }
    } catch(e) {
        alert("Load testing endpoint failed entirely. Is the server down?");
    }

    btnObj.innerText = oldText;
    btnObj.disabled = false;
    document.getElementById(otherBtnId).disabled = false;
}

document.getElementById('startLoadBlockingBtn').addEventListener('click', (e) => {
    triggerLoadTest('blocking', e.target, 'startLoadAsyncBtn');
});

document.getElementById('startLoadAsyncBtn').addEventListener('click', (e) => {
    triggerLoadTest('nonblocking', e.target, 'startLoadBlockingBtn');
});
