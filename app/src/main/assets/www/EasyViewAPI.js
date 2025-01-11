const EasyViewAPI = function (url) {
    this.url = url || `ws://${location.host}/api/v1`;
    this.ws = null
    this.Tasks = {};


    this.PersistentTask = async () => {
        await this.excute("UTILS$PING")
    }
    this.PersistentInterval = null
    this.enablePersistentTask = () => {
        this.PersistentTask();
        this.PersistentInterval = setInterval(this.PersistentTask, 4000); //nanoWSD IO_TIMEOUT_MS为5000
    }
    this.disablePersistentTask = () => {
        clearInterval(this.PersistentInterval);
    }

    this.guuid = () => {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
            var r = Math.random() * 16 | 0,
                v = c == 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        });
    }
    this.connect = async function () {
        console.log("New Connection Established");
        return new Promise((resolve, reject) => {
            this.ws = new WebSocket(this.url);
            this.ws.onopen = function () {
                resolve();
            };
            this.ws.onerror = function (err) {
                reject(err);
            };
            this.ws.onmessage = (event) => {
                const data = JSON.parse(event.data);
                console.log(data);
                if (data.id) {
                    if (data.id == "BROADCAST") {
                        console.log(this.events);
                        if (this.events.includes(data.action)) {
                            console.log(this.eventRegistered);
                            for (let callbacker of Object.values(this.eventRegistered[data.action])) callbacker(data.data);
                        }
                    }
                    if (this.Tasks[data.id]) {
                        const task = this.Tasks[data.id];
                        task.callbacker(data.status == "success", data.msg, data.data);
                        delete this.Tasks[data.id];
                    }
                }
            };
        });
    };

    this.actions = [
        "SYSTEM$GET_SYSTEM_INFO",
        "UTILS$PING_WITH_TIME",
        "UTILS$PING",
        "WIFI$WIFI_SCAN",
        "SYSTEM$GO_DESTROY"
    ]
    this.events = [
        "SYSTEM$GO_BACK"
    ]
    this.eventRegistered = {}
    this.events.forEach((event) => {
        this.eventRegistered[event] = {}
    });
    this.event = {}
    this.register = async function (event, callback) {
        if (!this.events.includes(event)) {
            throw new Error(`Event ${event} is not supported`);
        }
        const event_register_uuid = this.guuid();
        this.eventRegistered[event][event_register_uuid] = callback;
        console.log(this.eventRegistered);
        return event_register_uuid;
    }
    this.unregister = async function (event, event_register_uuid) {
        if (!this.events.includes(event)) {
            throw new Error(`Event ${event} is not supported`);
        }
        if (!this.eventRegistered[event][event_register_uuid]) {
            throw new Error(`Event ${event} with uuid ${event_register_uuid} is not registered`);
        }
        delete this.eventRegistered[event][event_register_uuid];
    }


    this.excute = async function (action, data) {
        // if (!this.actions.includes(action)) {
        //     throw new Error(`Action ${action} is not supported`);
        // }
        if (this.ws.readyState !== WebSocket.OPEN) {
            await this.connect();
        }
        return new Promise((resolve, reject) => {
            const task_uuid = this.guuid();
            this.Tasks[task_uuid] = {
                action: action,
                data: data,
                callbacker: (success, msg, data) => {
                    if (success) {
                        
                        resolve(data);
                    } else {
                        reject(new Error(msg));
                    }
                }
            };
            this.ws.send(JSON.stringify({
                id: task_uuid,
                action,
                data
            }));
        });
    };
};