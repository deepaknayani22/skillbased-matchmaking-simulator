![FinalSimV2-ezgif com-video-to-gif-converter](https://github.com/deepaknayani22/skillbased-matchmaking-simulator/assets/64520527/62209c6b-6fc1-4f93-9297-7a04483cf78b)

## Simulating Matchmaking in Online Multiplayer Games

This project, developed for CSE 561: Modeling & Simulation Theory and Application, focuses on creating a realistic simulation of a matchmaking system for online multiplayer games. Our simulation aims to enhance player satisfaction by efficiently matching players of similar skills, thus ensuring fairness and minimizing wait times in the queue.

## Abstract

The core of this simulation is a discrete event model that matches 10 players—5 on each side—with varying but reasonably similar skill levels. Our objective is to maximize fairness and minimize wait times, utilizing a modular, well-defined hierarchical system of subcomponents, each with its own specific role such as controlling queuing time distribution, maintaining and processing queues, and logging activity times.

## Introduction

With the significant prevalence of online gaming among Gen-Z and millennials, maintaining an enjoyable experience is critical. Our project simulates a queuing system that considers predefined skill levels and player behavior to match players in online games, focusing on high-level matchmaking concepts while abstracting away from less relevant details such as server-client latency and player demographics.

## System Description

The system comprises a central game server and players, with the server responsible for the matchmaking logic. It categorizes players into three skill tiers, each with a specific queue. The simulation models player behavior under various assumptions, including a fixed skill level, a patience threshold for waiting in queues, and the perception of satisfaction based solely on wait times and match fairness.

## Modeling

The simulation utilizes the DEVS-Suite Simulation Framework for its implementation, defining multiple components such as the Player Entity Generator, Matchmaking Handler, Team Assembler, and Activity Logger. These components interact within a defined hierarchy to simulate the matchmaking process, from player arrival to match formation.

## Experiments and Results

Several experiments were conducted to test the efficacy of the simulation, focusing on scenarios like queue capacity, player injection, and the impact of cross-tier matchmaking. The results highlight the benefits of cross-tier matchmaking in reducing average wait times and improving player satisfaction metrics.

## Contributions

This project is a collaborative effort by Aneesh Ahmed and Deepak Reddy Nayani. My contributions focused on problem identification, solution abstraction, matchmaking algorithm logic, and activity logging. The project helped solidify my understanding of object-oriented modeling and simulation concepts.

## Contact

For further inquiries or contributions to the project, please contact:

- Deepak Reddy Nayani
- School of Computing and Augmented Intelligence
- Arizona State University, Tempe, Arizona, USA
- Email: dnayani@asu.edu

## References

A list of references and resources utilized throughout the project development process.
