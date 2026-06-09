const endpoints = {
  habitats: "/api/habitats",
  modulos: "/api/modulos",
  sensores: "/api/sensores",
  ocupantes: "/api/ocupantes"
};

const state = {
  habitats: [],
  modulos: [],
  sensores: [],
  ocupantes: []
};

const els = {
  apiStatus: document.querySelector("#apiStatus"),
  habitatCount: document.querySelector("#habitatCount"),
  moduleCount: document.querySelector("#moduleCount"),
  sensorCount: document.querySelector("#sensorCount"),
  habitatList: document.querySelector("#habitatList"),
  moduleList: document.querySelector("#moduleList"),
  sensorList: document.querySelector("#sensorList"),
  occupantList: document.querySelector("#occupantList"),
  seedButton: document.querySelector("#seedButton"),
  toast: document.querySelector("#toast")
};

function showToast(message) {
  els.toast.textContent = message;
  els.toast.classList.add("show");
  window.setTimeout(() => els.toast.classList.remove("show"), 3200);
}

async function request(path, options = {}) {
  const response = await fetch(path, {
    headers: { "Content-Type": "application/json", ...(options.headers || {}) },
    ...options
  });

  if (!response.ok) {
    throw new Error(`Erro ${response.status} em ${path}`);
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
}

function pageContent(payload) {
  if (Array.isArray(payload)) {
    return payload;
  }

  return payload?.content || [];
}

function badgeClass(value = "") {
  const normalized = String(value).toLowerCase();
  if (normalized.includes("critico") || normalized.includes("crítico") || normalized.includes("inativo")) {
    return "danger";
  }
  if (normalized.includes("medio") || normalized.includes("médio") || normalized.includes("atencao") || normalized.includes("atenção")) {
    return "warn";
  }
  return "";
}

function renderList(target, items, renderer, emptyText) {
  target.innerHTML = "";

  if (!items.length) {
    const empty = document.createElement("div");
    empty.className = "empty";
    empty.textContent = emptyText;
    target.appendChild(empty);
    return;
  }

  items.slice(0, 8).forEach((item) => target.appendChild(renderer(item)));
}

function createItem(title, badge, lines) {
  const item = document.createElement("article");
  item.className = "item";

  const header = document.createElement("div");
  header.className = "item-header";
  header.innerHTML = `<strong>${title}</strong><span class="badge ${badgeClass(badge)}">${badge || "OK"}</span>`;

  const meta = document.createElement("div");
  meta.className = "meta";
  meta.innerHTML = lines.map((line) => `<span>${line}</span>`).join("");

  item.append(header, meta);
  return item;
}

function render() {
  els.habitatCount.textContent = state.habitats.length;
  els.moduleCount.textContent = state.modulos.length;
  els.sensorCount.textContent = state.sensores.length;

  renderList(
    els.habitatList,
    state.habitats,
    (habitat) =>
      createItem(habitat.nome, habitat.statusOperacional, [
        `Localização: ${habitat.localizacao || "-"}`,
        `Tipo: ${habitat.tipoHabitat || "-"}`,
        `Capacidade: ${habitat.capacidadeTotal ?? "-"}`
      ]),
    "Nenhum habitat cadastrado."
  );

  renderList(
    els.moduleList,
    state.modulos,
    (modulo) =>
      createItem(modulo.nomeModulo, modulo.nivelRisco, [
        `Habitat: ${modulo.nomeHabitat || "-"}`,
        `Ocupação: ${modulo.capacidadeAtual ?? 0}/${modulo.capacidadeOcupantes ?? 0}`,
        `Índice de risco: ${modulo.indiceRisco || "-"}`
      ]),
    "Nenhum módulo cadastrado."
  );

  renderList(
    els.sensorList,
    state.sensores,
    (sensor) =>
      createItem(sensor.nomeSensor, sensor.statusSensor, [
        `Módulo: ${sensor.nomeModulo || "-"}`,
        `Tipo: ${sensor.tipoSensor || "-"}`,
        `Faixa: ${sensor.limiteMinimo ?? "-"} a ${sensor.limiteMaximo ?? "-"} ${sensor.unidadeMedida || ""}`
      ]),
    "Nenhum sensor cadastrado."
  );

  renderList(
    els.occupantList,
    state.ocupantes,
    (ocupante) =>
      createItem(ocupante.nome, ocupante.statusOcupante, [
        `Função: ${ocupante.funcao || "-"}`,
        `Registro: ${ocupante.dataRegistro || "-"}`
      ]),
    "Nenhum ocupante cadastrado."
  );
}

async function loadResource(key) {
  const payload = await request(endpoints[key]);
  state[key] = pageContent(payload);
}

async function refreshAll() {
  try {
    await request("/api-docs");
    els.apiStatus.textContent = "Online";
    await Promise.all(Object.keys(endpoints).map(loadResource));
    render();
  } catch (error) {
    els.apiStatus.textContent = "Indisponível";
    showToast(error.message);
  }
}

async function seedData() {
  const stamp = Date.now();
  els.seedButton.disabled = true;
  els.seedButton.textContent = "Gerando...";

  try {
    const habitat = await request("/api/habitats", {
      method: "POST",
      body: JSON.stringify({
        nome: `Habitat Ares ${stamp}`,
        localizacao: "Marte",
        tipoHabitat: "Residencial",
        capacidadeTotal: 50,
        statusOperacional: "Ativo"
      })
    });

    const ocupante = await request("/api/ocupantes", {
      method: "POST",
      body: JSON.stringify({
        nome: `Operador Helios ${stamp}`,
        funcao: "Operador",
        statusOcupante: "ATIVO"
      })
    });

    const modulo = await request("/api/modulos", {
      method: "POST",
      body: JSON.stringify({
        idHabitat: habitat.id,
        nomeModulo: `Modulo Aurora ${stamp}`,
        tipoModulo: "Residencial",
        capacidadeOcupantes: 8,
        capacidadeAtual: 5,
        statusModulo: "ATIVO",
        nivelRisco: "BAIXO",
        indiceRisco: "12%"
      })
    });

    await request("/api/sensores", {
      method: "POST",
      body: JSON.stringify({
        idModulo: modulo.id,
        nomeSensor: `Sensor Temperatura ${stamp}`,
        tipoSensor: "TEMPERATURA",
        statusSensor: "ATIVO",
        unidadeMedida: "C",
        limiteMinimo: 18,
        limiteMaximo: 30,
        intervaloLeituraSegundos: 60
      })
    });

    await request("/api/reservas", {
      method: "POST",
      body: JSON.stringify({
        idOcupante: ocupante.id,
        idModulo: modulo.id,
        dataInicio: "2026-05-31",
        dataFim: "2026-06-07",
        statusReserva: "Ativa"
      })
    });

    showToast("Dados de demonstração criados com sucesso.");
    await refreshAll();
  } catch (error) {
    showToast(error.message);
  } finally {
    els.seedButton.disabled = false;
    els.seedButton.textContent = "Gerar dados";
  }
}

document.querySelectorAll("[data-refresh]").forEach((button) => {
  button.addEventListener("click", async () => {
    const key = button.dataset.refresh;
    await loadResource(key);
    render();
    showToast("Lista atualizada.");
  });
});

els.seedButton.addEventListener("click", seedData);
refreshAll();
