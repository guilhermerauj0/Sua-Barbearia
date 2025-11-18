# 🎨 Guia de Integração Frontend - Perfis de Funcionários

**Projeto:** Sua Barbearia API  
**Data:** 18 de novembro de 2025  
**Objetivo:** Orientações para apresentar perfis de funcionários no frontend

---

## 📋 ESTRUTURA DAS ROTAS DE API

### **1. Listar Funcionários**

**Endpoint:** `GET /api/barbearias/meus-funcionarios`

**Headers:**
```http
Authorization: Bearer {JWT_TOKEN}
```

**Resposta de Sucesso (200):**
```json
[
  {
    "id": 1,
    "barbeariaId": 10,
    "nome": "Carlos Silva",
    "email": "carlos@barbearia.com",
    "telefone": "(11) 98765-4321",
    "perfilType": "BARBEIRO",
    "profissao": "BARBEIRO",
    "especialidades": "Cortes de cabelo e barba",
    "ativo": true,
    "dataCriacao": "2025-11-15T10:30:00",
    "dataAtualizacao": "2025-11-15T10:30:00"
  },
  {
    "id": 2,
    "barbeariaId": 10,
    "nome": "Ana Costa",
    "email": "ana@barbearia.com",
    "telefone": "(11) 91234-5678",
    "perfilType": "MANICURE",
    "profissao": "MANICURE",
    "especialidades": "Manicure e pedicure",
    "ativo": true,
    "dataCriacao": "2025-11-16T14:00:00",
    "dataAtualizacao": "2025-11-16T14:00:00"
  }
]
```

---

### **2. Criar Funcionário**

**Endpoint:** `POST /api/barbearias/meus-funcionarios`

**Headers:**
```http
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

**Body da Requisição:**
```json
{
  "nome": "Maria Santos",
  "email": "maria@barbearia.com",
  "telefone": "(11) 99999-8888",
  "perfilType": "ESTETICISTA"
}
```

**Resposta de Sucesso (201):**
```json
{
  "id": 3,
  "barbeariaId": 10,
  "nome": "Maria Santos",
  "email": "maria@barbearia.com",
  "telefone": "(11) 99999-8888",
  "perfilType": "ESTETICISTA",
  "profissao": "ESTETICISTA",
  "especialidades": "Design de sobrancelhas e estética facial",
  "ativo": true,
  "dataCriacao": "2025-11-18T15:45:00",
  "dataAtualizacao": "2025-11-18T15:45:00"
}
```

**Erro - Email Duplicado (400):**
```json
"Já existe um funcionário com este email nesta barbearia"
```

---

## 🎨 MELHORES PRÁTICAS PARA O FRONTEND

### **1️⃣ Tipos de Perfil Disponíveis**

Crie um **ENUM ou CONSTANTE** no frontend que espelhe os tipos do backend:

```typescript
// TypeScript/JavaScript
export enum TipoPerfil {
  BARBEIRO = 'BARBEIRO',
  MANICURE = 'MANICURE',
  ESTETICISTA = 'ESTETICISTA',
  COLORISTA = 'COLORISTA'
}

// Configuração para exibição
export const PERFIL_CONFIG = {
  BARBEIRO: {
    label: 'Barbeiro',
    descricao: 'Barbeiro',
    especialidades: 'Cortes de cabelo e barba',
    icon: '💈',
    color: '#3B82F6', // Azul
    badge: 'primary'
  },
  MANICURE: {
    label: 'Manicure',
    descricao: 'Manicure',
    especialidades: 'Manicure e pedicure',
    icon: '💅',
    color: '#EC4899', // Rosa
    badge: 'secondary'
  },
  ESTETICISTA: {
    label: 'Esteticista',
    descricao: 'Esteticista',
    especialidades: 'Design de sobrancelhas e estética facial',
    icon: '✨',
    color: '#8B5CF6', // Roxo
    badge: 'info'
  },
  COLORISTA: {
    label: 'Colorista',
    descricao: 'Colorista',
    especialidades: 'Coloração capilar e tratamentos químicos',
    icon: '🎨',
    color: '#F59E0B', // Laranja
    badge: 'warning'
  }
};
```

---

### **2️⃣ Interface/Type para Funcionário**

```typescript
// TypeScript
export interface Funcionario {
  id: number;
  barbeariaId: number;
  nome: string;
  email: string;
  telefone: string;
  perfilType: TipoPerfil;
  profissao: string;
  especialidades: string;
  ativo: boolean;
  dataCriacao: string; // ou Date após parse
  dataAtualizacao: string; // ou Date após parse
}

// Função helper para obter configuração do perfil
export function getPerfilConfig(perfilType: TipoPerfil) {
  return PERFIL_CONFIG[perfilType];
}
```

---

### **3️⃣ Componente de Card de Funcionário (React/Vue)**

#### **Exemplo React:**

```tsx
import React from 'react';
import { Funcionario, getPerfilConfig } from './types';

interface FuncionarioCardProps {
  funcionario: Funcionario;
  onEdit?: (id: number) => void;
  onDelete?: (id: number) => void;
}

export const FuncionarioCard: React.FC<FuncionarioCardProps> = ({ 
  funcionario, 
  onEdit, 
  onDelete 
}) => {
  const config = getPerfilConfig(funcionario.perfilType);
  
  return (
    <div className="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition-shadow">
      {/* Header com ícone e badge */}
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center gap-3">
          <span className="text-4xl">{config.icon}</span>
          <div>
            <h3 className="text-lg font-semibold text-gray-900">
              {funcionario.nome}
            </h3>
            <span 
              className={`inline-block px-3 py-1 text-xs font-medium rounded-full`}
              style={{ backgroundColor: config.color + '20', color: config.color }}
            >
              {config.label}
            </span>
          </div>
        </div>
        
        {/* Status */}
        {funcionario.ativo && (
          <span className="px-2 py-1 bg-green-100 text-green-800 text-xs rounded-full">
            Ativo
          </span>
        )}
      </div>
      
      {/* Especialidades */}
      <p className="text-sm text-gray-600 mb-3">
        <strong>Especialidades:</strong> {funcionario.especialidades}
      </p>
      
      {/* Contato */}
      <div className="space-y-2 text-sm text-gray-700">
        <div className="flex items-center gap-2">
          <span>📧</span>
          <span>{funcionario.email}</span>
        </div>
        <div className="flex items-center gap-2">
          <span>📱</span>
          <span>{funcionario.telefone}</span>
        </div>
      </div>
      
      {/* Ações */}
      {(onEdit || onDelete) && (
        <div className="flex gap-2 mt-4 pt-4 border-t">
          {onEdit && (
            <button 
              onClick={() => onEdit(funcionario.id)}
              className="flex-1 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
            >
              Editar
            </button>
          )}
          {onDelete && (
            <button 
              onClick={() => onDelete(funcionario.id)}
              className="flex-1 px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600"
            >
              Remover
            </button>
          )}
        </div>
      )}
    </div>
  );
};
```

---

### **4️⃣ Formulário de Criação/Edição**

```tsx
import React, { useState } from 'react';
import { TipoPerfil, PERFIL_CONFIG } from './types';

interface FuncionarioFormData {
  nome: string;
  email: string;
  telefone: string;
  perfilType: TipoPerfil;
}

export const FuncionarioForm: React.FC = () => {
  const [formData, setFormData] = useState<FuncionarioFormData>({
    nome: '',
    email: '',
    telefone: '',
    perfilType: TipoPerfil.BARBEIRO
  });
  
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    try {
      const response = await fetch('/api/barbearias/meus-funcionarios', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
        body: JSON.stringify(formData)
      });
      
      if (response.ok) {
        const funcionario = await response.json();
        console.log('Funcionário criado:', funcionario);
        // Redirecionar ou atualizar lista
      } else {
        const error = await response.text();
        console.error('Erro:', error);
      }
    } catch (error) {
      console.error('Erro ao criar funcionário:', error);
    }
  };
  
  return (
    <form onSubmit={handleSubmit} className="space-y-4 max-w-md">
      {/* Nome */}
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">
          Nome Completo *
        </label>
        <input
          type="text"
          value={formData.nome}
          onChange={(e) => setFormData({ ...formData, nome: e.target.value })}
          className="w-full px-3 py-2 border rounded-md"
          required
          minLength={3}
          maxLength={100}
        />
      </div>
      
      {/* Email */}
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">
          Email *
        </label>
        <input
          type="email"
          value={formData.email}
          onChange={(e) => setFormData({ ...formData, email: e.target.value })}
          className="w-full px-3 py-2 border rounded-md"
          required
        />
      </div>
      
      {/* Telefone */}
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">
          Telefone *
        </label>
        <input
          type="tel"
          value={formData.telefone}
          onChange={(e) => setFormData({ ...formData, telefone: e.target.value })}
          className="w-full px-3 py-2 border rounded-md"
          placeholder="(11) 98765-4321"
          required
        />
      </div>
      
      {/* Tipo de Perfil */}
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-2">
          Tipo de Perfil *
        </label>
        <div className="grid grid-cols-2 gap-3">
          {Object.entries(PERFIL_CONFIG).map(([tipo, config]) => (
            <label
              key={tipo}
              className={`
                flex items-center gap-3 p-3 border-2 rounded-lg cursor-pointer
                transition-all hover:border-gray-400
                ${formData.perfilType === tipo 
                  ? 'border-blue-500 bg-blue-50' 
                  : 'border-gray-200'
                }
              `}
            >
              <input
                type="radio"
                name="perfilType"
                value={tipo}
                checked={formData.perfilType === tipo}
                onChange={(e) => setFormData({ 
                  ...formData, 
                  perfilType: e.target.value as TipoPerfil 
                })}
                className="sr-only"
              />
              <span className="text-2xl">{config.icon}</span>
              <div>
                <div className="font-medium text-sm">{config.label}</div>
                <div className="text-xs text-gray-500">{config.especialidades}</div>
              </div>
            </label>
          ))}
        </div>
      </div>
      
      {/* Botão Submit */}
      <button
        type="submit"
        className="w-full px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
      >
        Cadastrar Funcionário
      </button>
    </form>
  );
};
```

---

### **5️⃣ Lista de Funcionários com Filtros**

```tsx
import React, { useEffect, useState } from 'react';
import { Funcionario, TipoPerfil } from './types';
import { FuncionarioCard } from './FuncionarioCard';

export const FuncionariosList: React.FC = () => {
  const [funcionarios, setFuncionarios] = useState<Funcionario[]>([]);
  const [filtro, setFiltro] = useState<TipoPerfil | 'TODOS'>('TODOS');
  const [loading, setLoading] = useState(true);
  
  useEffect(() => {
    fetchFuncionarios();
  }, []);
  
  const fetchFuncionarios = async () => {
    try {
      const response = await fetch('/api/barbearias/meus-funcionarios', {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });
      
      if (response.ok) {
        const data = await response.json();
        setFuncionarios(data);
      }
    } catch (error) {
      console.error('Erro ao carregar funcionários:', error);
    } finally {
      setLoading(false);
    }
  };
  
  const funcionariosFiltrados = filtro === 'TODOS'
    ? funcionarios
    : funcionarios.filter(f => f.perfilType === filtro);
  
  // Contagem por perfil
  const contadores = {
    BARBEIRO: funcionarios.filter(f => f.perfilType === 'BARBEIRO').length,
    MANICURE: funcionarios.filter(f => f.perfilType === 'MANICURE').length,
    ESTETICISTA: funcionarios.filter(f => f.perfilType === 'ESTETICISTA').length,
    COLORISTA: funcionarios.filter(f => f.perfilType === 'COLORISTA').length
  };
  
  return (
    <div className="p-6">
      {/* Header */}
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-gray-900 mb-2">
          Meus Funcionários ({funcionarios.length})
        </h1>
        
        {/* Filtros */}
        <div className="flex gap-2 flex-wrap">
          <button
            onClick={() => setFiltro('TODOS')}
            className={`px-4 py-2 rounded-full text-sm font-medium ${
              filtro === 'TODOS' 
                ? 'bg-gray-800 text-white' 
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
            }`}
          >
            Todos ({funcionarios.length})
          </button>
          
          <button
            onClick={() => setFiltro(TipoPerfil.BARBEIRO)}
            className={`px-4 py-2 rounded-full text-sm font-medium ${
              filtro === TipoPerfil.BARBEIRO 
                ? 'bg-blue-600 text-white' 
                : 'bg-blue-100 text-blue-700 hover:bg-blue-200'
            }`}
          >
            💈 Barbeiros ({contadores.BARBEIRO})
          </button>
          
          <button
            onClick={() => setFiltro(TipoPerfil.MANICURE)}
            className={`px-4 py-2 rounded-full text-sm font-medium ${
              filtro === TipoPerfil.MANICURE 
                ? 'bg-pink-600 text-white' 
                : 'bg-pink-100 text-pink-700 hover:bg-pink-200'
            }`}
          >
            💅 Manicures ({contadores.MANICURE})
          </button>
          
          <button
            onClick={() => setFiltro(TipoPerfil.ESTETICISTA)}
            className={`px-4 py-2 rounded-full text-sm font-medium ${
              filtro === TipoPerfil.ESTETICISTA 
                ? 'bg-purple-600 text-white' 
                : 'bg-purple-100 text-purple-700 hover:bg-purple-200'
            }`}
          >
            ✨ Esteticistas ({contadores.ESTETICISTA})
          </button>
          
          <button
            onClick={() => setFiltro(TipoPerfil.COLORISTA)}
            className={`px-4 py-2 rounded-full text-sm font-medium ${
              filtro === TipoPerfil.COLORISTA 
                ? 'bg-orange-600 text-white' 
                : 'bg-orange-100 text-orange-700 hover:bg-orange-200'
            }`}
          >
            🎨 Coloristas ({contadores.COLORISTA})
          </button>
        </div>
      </div>
      
      {/* Grid de Cards */}
      {loading ? (
        <div className="text-center py-12">Carregando...</div>
      ) : funcionariosFiltrados.length === 0 ? (
        <div className="text-center py-12 text-gray-500">
          Nenhum funcionário encontrado
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {funcionariosFiltrados.map(funcionario => (
            <FuncionarioCard 
              key={funcionario.id} 
              funcionario={funcionario}
            />
          ))}
        </div>
      )}
    </div>
  );
};
```

---

## 📱 COMPONENTES PARA MOBILE (React Native)

```tsx
import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';
import { Funcionario, getPerfilConfig } from './types';

interface FuncionarioItemProps {
  funcionario: Funcionario;
  onPress?: () => void;
}

export const FuncionarioItem: React.FC<FuncionarioItemProps> = ({ 
  funcionario, 
  onPress 
}) => {
  const config = getPerfilConfig(funcionario.perfilType);
  
  return (
    <TouchableOpacity 
      style={styles.container}
      onPress={onPress}
      activeOpacity={0.7}
    >
      <View style={styles.header}>
        <Text style={styles.icon}>{config.icon}</Text>
        <View style={styles.info}>
          <Text style={styles.nome}>{funcionario.nome}</Text>
          <View style={[styles.badge, { backgroundColor: config.color + '20' }]}>
            <Text style={[styles.badgeText, { color: config.color }]}>
              {config.label}
            </Text>
          </View>
        </View>
      </View>
      
      <Text style={styles.especialidades}>{funcionario.especialidades}</Text>
      
      <View style={styles.contato}>
        <Text style={styles.contatoText}>📧 {funcionario.email}</Text>
        <Text style={styles.contatoText}>📱 {funcionario.telefone}</Text>
      </View>
    </TouchableOpacity>
  );
};

const styles = StyleSheet.create({
  container: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 16,
    marginBottom: 12,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 12,
  },
  icon: {
    fontSize: 40,
    marginRight: 12,
  },
  info: {
    flex: 1,
  },
  nome: {
    fontSize: 16,
    fontWeight: '600',
    color: '#1f2937',
    marginBottom: 4,
  },
  badge: {
    alignSelf: 'flex-start',
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 12,
  },
  badgeText: {
    fontSize: 12,
    fontWeight: '500',
  },
  especialidades: {
    fontSize: 14,
    color: '#6b7280',
    marginBottom: 8,
  },
  contato: {
    gap: 4,
  },
  contatoText: {
    fontSize: 13,
    color: '#4b5563',
  },
});
```

---

## 🎯 RESUMO - MELHORES PRÁTICAS

### ✅ **O QUE FAZER:**

1. **Criar enum/constante local** espelhando `TipoPerfil` do backend
2. **Usar cores e ícones consistentes** para cada tipo de perfil
3. **Exibir especialidades** junto ao nome/tipo do perfil
4. **Implementar filtros visuais** para facilitar navegação
5. **Validar no frontend** antes de enviar (email válido, campos obrigatórios)
6. **Usar badges/chips coloridos** para destacar o tipo de perfil
7. **Mostrar contadores** por tipo de perfil
8. **Feedback visual** ao criar/editar (loading, sucesso, erro)

### ❌ **O QUE EVITAR:**

1. **NÃO criar valores de perfil diferentes** do backend (sempre usar BARBEIRO, MANICURE, ESTETICISTA, COLORISTA)
2. **NÃO exibir apenas o enum** sem tradução visual (use labels amigáveis)
3. **NÃO misturar `perfilType` com `profissao`** - ambos vêm do backend já formatados
4. **NÃO permitir edição do tipo de perfil** após criação (pode gerar inconsistências)
5. **NÃO esquecer de tratar erros** de email duplicado

---

## 📊 EXEMPLO DE DASHBOARD

```
┌─────────────────────────────────────────────────────────┐
│  Meus Funcionários (8)                         + Novo   │
├─────────────────────────────────────────────────────────┤
│  [Todos (8)] [💈 Barbeiros (3)] [💅 Manicures (2)]     │
│  [✨ Esteticistas (2)] [🎨 Coloristas (1)]             │
├─────────────────────────────────────────────────────────┤
│  ┌───────────────┐  ┌───────────────┐  ┌──────────────┐│
│  │ 💈 Carlos     │  │ 💈 João       │  │ 💈 Pedro     ││
│  │ BARBEIRO      │  │ BARBEIRO      │  │ BARBEIRO     ││
│  │ Cortes/barba  │  │ Cortes/barba  │  │ Cortes/barba ││
│  │ 📧 carlos@... │  │ 📧 joao@...   │  │ 📧 pedro@... ││
│  │ 📱 (11) 98... │  │ 📱 (11) 97... │  │ 📱 (11) 96...││
│  └───────────────┘  └───────────────┘  └──────────────┘│
│  ┌───────────────┐  ┌───────────────┐                  │
│  │ 💅 Ana        │  │ 💅 Maria      │                  │
│  │ MANICURE      │  │ MANICURE      │                  │
│  │ Manicure/pedi │  │ Manicure/pedi │                  │
│  │ 📧 ana@...    │  │ 📧 maria@...  │                  │
│  │ 📱 (11) 95... │  │ 📱 (11) 94... │                  │
│  └───────────────┘  └───────────────┘                  │
└─────────────────────────────────────────────────────────┘
```

---

**Este guia fornece todas as informações necessárias para integrar os perfis de funcionários no frontend de forma consistente e profissional!** 🚀
