# Mechatronics Engineering Portfolio

Welcome! I am a 3rd year Mechatronics Engineering student at the German University in Cairo (GUC). This master repository serves as a centralized hub documentation for my academic work, personal projects, and engineering research across robotics, embedded systems, automation, and software engineering.

---

## 🚀 Featured Projects

### 🛠️ 1. Custom 16-Bit Hardwired CPU Architecture Design (Mano-Machine Variant)
Designed, modeled, and verified a custom 16-bit register-transfer-level (RTL) processor architecture using Logisim. The system features a customized ISA engineered to execute compiled iterative loop programs directly at the hardware gate level.

#### 📊 Architectural Subsystem & Design Implementation
* **Central Datapath & Bus Topology**: Configured synchronized data routing across core execution registers (**PC, AR, DR, AC, IR, TR**) linked through a 16-bit common bus architecture managed by a 3-bit selection multiplexer matrix ($S_2, S_1, S_0$).
* **Custom ISA Opcodes (`XOR` & `DIV`)**: Re-engineered the control unit execution mapping by repurposing standard hardware opcodes to handle custom, low-level multi-bit XOR logic and a hardware-level division pipeline.
* **Hardwired Control Unit Decoder**: Engineered a synchronous control matrix combining a Sequence Counter (SC), instruction decoders ($D_0 - D_{15}$), and precise timing cycles ($T_0 - T_7$). Implemented custom combinational logic expressions to automate the exact execution controls for every Register Load (`LD`), Increment (`INR`), Clear (`CLR`), and Memory Read/Write cycle.

#### 📂 Project Deliverables & Academic Specifications
* 💾 **Source File**: [Download raw circuit design (.circ)](./CPU-Architecture/cpu_design.circ)
* 📄 **Milestone 1 Documentation**: [View RTL Micro-Operations & Bus Controls Portfolio PDF](./CPU-Architecture/Milestone_1_Design.pdf)
* 📄 **Milestone 2 Documentation**: [View Core Instruction Timing & Control Expressions PDF](./CPU-Architecture/Milestone_2_Implementation.pdf)

---

## 🛠️ Core Tech Stack & Tools
* **CAD/CAM/CNC**: SolidWorks, Fusion 360, CNC G-Code, Siemens SINUMERIK 840D sl
* **Electronics & Logic Simulation**: PSpice Analog Simulation, Logisim RTL Design, Circuit Verification
* **Programming Languages**: Java (Object-Oriented Programming), Python, C

---

## 📬 Connect With Me
* **LinkedIn**: https://www.linkedin.com/in/zina-zaki-59a3203b1/
* **Email**: zwmzaki2006@gmail.com

