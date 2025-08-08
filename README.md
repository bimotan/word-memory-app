# Smart Word Learning (CLI) · 智能单词学习系统（命令行）

A lightweight, **spaced‑repetition** vocabulary trainer written in **Java**.  
一个用 **Java** 编写的轻量级**间隔重复**背单词工具。

---

## ✨ Features · 功能特性

- **Spaced Repetition (SM‑2 inspired)**: updates *easiness factor* and *review interval* based on answer quality.  
  **间隔重复（受 SM‑2 启发）**：根据答题“质量分”动态更新**易度因子（EF）**与**复习间隔**。

- **Memory strength modeling**: exponential decay using hours since last review, *easiness*, and *streak*.  
  **记忆强度建模**：结合上次复习后的小时数、易度因子与连续答对次数做指数衰减。

- **Smart review scheduling**: picks next word by **urgency‑weighted random** (weak memory, new or overdue words get priority).  
  **智能抽词**：按**紧迫度加权随机**选择待复习词（记忆弱、新词、超期词优先）。

- **Plain‑text storage**: all data saved as `;`‑separated lines in a `.txt` file in current folder.  
  **纯文本存储**：所有数据保存在当前目录下的 `.txt` 文件（分号分隔字段）。

- **Simple CLI** (Chinese prompts): add words, review, list all.  
  **简洁命令行界面**（中文提示）：添加单词、复习、查看列表。

---

## 🧠 Core Algorithms · 核心算法

### 1) Spaced Repetition Update · 间隔重复更新（`SpacedRepetition.updateMemory`）
- Input quality ∈ {1,2,3,4,5}（由答案与正确中文的相似度映射而来）。  
  质量分（1–5）由用户答案与标准中文释义的**相似度**映射得到（见下方“评分映射”）。
- If **quality < 3** (wrong):  
  若 **quality < 3**（回答错误）：
  - `consecutiveCorrect = 0`，`interval = 1 天`
- Else (correct):  
  否则（回答正确）：
  - `consecutiveCorrect += 1`
  - Update **easiness**:  
    `EF = max( MIN_EASINESS=1.3,  EF + (0.1 - (5 - q) * (0.08 + (5 - q)*0.02)) )`
  - Set **interval**:  
    First 3 correct reps use `(1, 3, 7)` days; afterwards `interval *= EF`.  
    前 3 次正确间隔依次为 `(1, 3, 7)` 天；之后 `interval *= EF`。

### 2) Memory Strength · 记忆强度（`Word.calculateMemoryStrength`）
`hoursSinceReview = (now - lastReviewed) / 3600`  
`stability = 5.0 * easiness * (1 + consecutiveCorrect/10.0)`  
`memoryStrength = exp( - hoursSinceReview / stability )`  
> 值域在 (0,1]，随时间衰减，易度与连续答对次数会提高稳定性。

### 3) Review Word Selection · 抽词策略（`WordSelector.selectReviewWord`）
For each word, compute **urgency**:  
对于每个单词计算**紧迫度**：  
`base = 1 - memoryStrength`  
`novelty = 1.5  if consecutiveCorrect < 3  else 1.0`  
`overdue = 1 + overdueDays * 0.2  (if past due; else 1)`  
`urgency = base * novelty * overdue`  
Finally pick a word by **weighted random** over urgencies.  
最终按**权重随机**抽取紧迫度更高的词。

### 4) Similarity → Quality Mapping · 相似度到质量分映射（`WordManager`）
- Similarity uses **character‑set Jaccard** over user answer vs. correct Chinese.  
  相似度：基于字符集合的 **Jaccard**（交并比）。
- Mapping: `>0.9→5`, `>0.7→4`, `>0.5→3`, `>0.3→2`, otherwise `1`。

---

## 🗂 Data Format · 数据格式

Each line in the `.txt` file (semicolon `;` separated):  
每行一条记录（以分号 `;` 分隔）：

```
english;chinese;addedDate;lastReviewed;easiness;interval;consecutiveCorrect
```

- `addedDate` / `lastReviewed` format: `yyyy-MM-dd HH:mm:ss`  
- Files live in the **current working directory**. On start you can **choose** an existing `.txt` file or **create** a new one.  
  文件在**当前工作目录**。程序启动可**选择**已有 `.txt` 或**创建**新文件；若选择无效则使用 `default_words.txt`。

---

## 🚀 Build & Run · 构建与运行

**Prerequisite · 先决条件**: Java 8+ (JDK).

```bash
# compile · 编译
javac *.java

# run · 运行
java MainApp
```

Menu · 菜单：  
1) 添加新单词（输入英文与中文）  
2) 复习单词（按提示作答；系统自动评分并更新记忆曲线）  
3) 查看所有单词（带“记忆强度%”）  
4) 退出

> 提示：首次运行可选择或新建 `.txt` 单词库文件。

---

## 🧩 Project Structure · 项目结构

- `MainApp.java` — CLI 入口与菜单。  
- `Word.java` — 单词实体：`english`, `chinese`, `addedDate`, `lastReviewed`, `easiness`, `interval`, `consecutiveCorrect`；含 `calculateMemoryStrength()`。
- `SpacedRepetition.java` — SM‑2 风格的间隔重复更新。  
- `WordManager.java` — 增删查存、复习流程、相似度计算与质量分映射、文件 I/O。  
- `WordSelector.java` — 基于记忆强度/新鲜度/超期的紧迫度加权抽词。  
- `FileUtils.java` — 枚举/创建文本文件等。  
- `InputUtils.java` — 控制台输入工具。

---

## 📝 Example · 使用示例

```
=== 智能单词学习系统 ===

请选择操作:
1. 添加新单词
2. 复习单词
3. 查看所有单词
4. 退出
```

复习时：输入中文释义 → 程序显示正确答案与你的答案 → 计算相似度并映射质量分（1–5） → 更新 EF/间隔与记忆强度。

---

## 🛠 Roadmap · 迭代计划（建议）

- [ ] Add import/export (CSV/JSON) · 支持 CSV/JSON 导入导出  
- [ ] Multiple decks & tags · 支持多词库和标签  
- [ ] Synonyms & multiple answers · 多标准答案/同义词  
- [ ] GUI (JavaFX/Swing) · 图形界面  
- [ ] Unit tests & CI · 单元测试与持续集成

---

## 📦 Getting Ready for GitHub · GitHub 上传指引

1. Create a new repo on GitHub (Public). 新建公开仓库  
2. Locally put `.java` files and this `README.md` in repo folder. 将源码与 README 放入仓库目录  
3. Add a `.gitignore` (see below). 添加 `.gitignore`  
4. (Optional) Add `LICENSE` (MIT) and a **screenshot** folder. 可选加入 MIT 许可证与截图  
5. Commit & Push 提交并推送：

```bash
git init
git add .
git commit -m "init: smart word learning CLI"
git branch -M main
git remote add origin https://github.com/bimotan/<repo-name>.git
git push -u origin main
```

---


---

## 👤 Author · 作者
**Xinrun Li**

## 📄 License · 许可证
This project is licensed under the MIT License — see the LICENSE file for details.
本项目采用 MIT 许可证，详情见 LICENSE 文件。
