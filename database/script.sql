CREATE DATABASE gestao_projetos;
USE gestao_projetos;

-- Tabela de Usuários
CREATE TABLE usuarios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome_completo VARCHAR(100) NOT NULL,
    cpf VARCHAR(14) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    cargo VARCHAR(50) NOT NULL,
    login VARCHAR(50) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    perfil ENUM('administrador', 'gerente', 'colaborador') NOT NULL
);

-- Tabela de Projetos
CREATE TABLE projetos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT,
    data_inicio DATE NOT NULL,
    data_termino_prevista DATE NOT NULL,
    status ENUM('planejado', 'em_andamento', 'concluido', 'cancelado') DEFAULT 'planejado',
    gerente_id INT,
    FOREIGN KEY (gerente_id) REFERENCES usuarios(id)
);

-- Tabela de Equipes
CREATE TABLE equipes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT
);

-- Tabela de relação entre Equipes e Membros
CREATE TABLE equipe_membros (
    equipe_id INT,
    usuario_id INT,
    PRIMARY KEY (equipe_id, usuario_id),
    FOREIGN KEY (equipe_id) REFERENCES equipes(id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Tabela de relação entre Projetos e Equipes
CREATE TABLE projeto_equipes (
    projeto_id INT,
    equipe_id INT,
    PRIMARY KEY (projeto_id, equipe_id),
    FOREIGN KEY (projeto_id) REFERENCES projetos(id),
    FOREIGN KEY (equipe_id) REFERENCES equipes(id)
);

-- Tabela de Tarefas
CREATE TABLE tarefas (
    id INT PRIMARY KEY AUTO_INCREMENT,
    titulo VARCHAR(100) NOT NULL,
    descricao TEXT,
    projeto_id INT NOT NULL,
    responsavel_id INT NOT NULL,
    status ENUM('pendente', 'em_execucao', 'concluida') DEFAULT 'pendente',
    data_inicio DATE,
    data_fim_prevista DATE,
    data_fim_real DATE,
    FOREIGN KEY (projeto_id) REFERENCES projetos(id),
    FOREIGN KEY (responsavel_id) REFERENCES usuarios(id)
);

-- Tabela de histórico de alterações de tarefas
CREATE TABLE historico_tarefas (
    id INT PRIMARY KEY AUTO_INCREMENT,
    tarefa_id INT NOT NULL,
    status_anterior VARCHAR(20),
    status_novo VARCHAR(20) NOT NULL,
    usuario_id INT NOT NULL,
    data_alteracao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    observacao TEXT,
    FOREIGN KEY (tarefa_id) REFERENCES tarefas(id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Tabela de logs do sistema
CREATE TABLE logs_sistema (
    id INT PRIMARY KEY AUTO_INCREMENT,
    usuario_id INT,
    acao VARCHAR(100) NOT NULL,
    tabela_afetada VARCHAR(50),
    registro_id INT,
    data_acao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Inserir usuário administrador padrão
INSERT INTO usuarios (nome_completo, cpf, email, cargo, login, senha, perfil) 
VALUES ('Administrador', '000.000.000-00', 'admin@email.com', 'Administrador', 'admin', 'admin123', 'administrador');