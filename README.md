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

#### 📂 Project Deliverables
* 💾 **Source File**: [Download raw circuit design (.circ)](./Custom%2016-Bit%20Hardwired%20CPU%20Architecture%20Design/CPU%20Design.circ)
* 📄 **Milestone 1 Documentation**: [View RTL Micro-Operations & Bus Controls Portfolio PDF](./Custom%2016-Bit%20Hardwired%20CPU%20Architecture%20Design/Milestone%201%20Description%20.pdf)
* 📄 **Milestone 2 Documentation**: [View Core Instruction Timing & Control Expressions PDF](./Custom%2016-Bit%20Hardwired%20CPU%20Architecture%20Design/Milestone%202%20Description.pdf)

#### 📊 System Architecture Schematics

| 1. Complete Processor Datapath | 2. Custom 16-Bit ALU |
| :---: | :---: |
| ![Datapath](./Custom%2016-Bit%20Hardwired%20CPU%20Architecture%20Design/Images/Main%20.png) | ![ALU](./Custom%2016-Bit%20Hardwired%20CPU%20Architecture%20Design/Images/ALU.png) |

| 3. Hardwired Control Unit (Decode) | 4. Sequential & Timing Logic |
| :---: | :---: |
| ![Control Unit](./Custom%2016-Bit%20Hardwired%20CPU%20Architecture%20Design/Images/CU1.png) | ![Timing Logic](./Custom%2016-Bit%20Hardwired%20CPU%20Architecture%20Design/Images/CU3.png) |

---
### ⚡ 2. Active Cascaded Band-Pass Filter Design & Hardware Verification
Designed, simulated, and prototyped a multi-stage active cascaded band-pass filter targeting a center frequency ($f_c$) of 7.58 kHz with unity gain ($A_v = 1$), achieving a 9.5/10 project evaluation grade.

#### 📊 Engineering Workflow & Implementation
* **Theoretical Design & Sourcing Constraints**: Calculated exact component frequencies on paper and successfully re-mapped mathematical resistor/capacitor networks to match standard commercial market values without disrupting tuning limits.
* **SPICE Simulation Verification**: Modeled the dual LM324 operational amplifier topology within PSpice. Ran AC Sweep analyses to verify band-pass bandwidth characteristics and conducted transient analyses to validate time-domain wave symmetry.
* **Hardware Prototyping & Bench Testing**: Constructed the physical active network on a breadboard using decoupled dual $\pm$12V DC power rails. 
* **Signal Validation**: Utilized an arbitrary function generator to inject test frequencies and analyzed output attenuation, phase alignment, and voltage gains using a hardware analog oscilloscope to confirm parity with simulation models.

#### 📊 Simulation & Experimental Reference Sheets

| PSpice Schematic Design | AC Sweep Frequency Response | Transient Analysis Waveforms |
| :---: | :---: | :---: |
| ![PSpice Schematic](./Cascaded%20Active%20Filter%20Design/Picture5.png) | ![AC Sweep Curve](./Cascaded%20Active%20Filter%20Design/Picture6.png) | ![Transient Plot](./Cascaded%20Active%20Filter%20Design/Picture7.png) |

| Physical Breadboard Layout | Hardware Bench Setup | Oscilloscope Analysis |
| :---: | :---: | :---: |
| ![Breadboard Top](./Cascaded%20Active%20Filter%20Design/Picture1.jpg) | ![Breadboard Side](./Cascaded%20Active%20Filter%20Design/Picture2.jpg) | ![Oscilloscope Reading](./Cascaded%20Active%20Filter%20Design/Picture3.jpg) |
---


## 🛠️ Core Tech Stack & Tools
* **CAD/CAM/CNC**: SolidWorks, Fusion 360, CNC G-Code, Siemens SINUMERIK 840D sl
* **Electronics & Logic Simulation**: PSpice Analog Simulation, Logisim RTL Design, Circuit Verification
* **Programming Languages**: Java (Object-Oriented Programming), Python, C

---

## 📬 Connect With Me
* **LinkedIn**: https://www.linkedin.com/in/zina-zaki-59a3203b1/
* **Email**: zwmzaki2006@gmail.com

