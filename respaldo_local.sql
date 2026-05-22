-- MySQL dump 10.13  Distrib 9.2.0, for macos15.2 (arm64)
--
-- Host: localhost    Database: DesarrolloWEB
-- ------------------------------------------------------
-- Server version	9.2.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `AuditLog`
--

DROP TABLE IF EXISTS `AuditLog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `AuditLog` (
  `auditID` bigint NOT NULL AUTO_INCREMENT,
  `action` varchar(255) NOT NULL,
  `changeDate` datetime(6) NOT NULL,
  `entityID` bigint NOT NULL,
  `entityType` varchar(255) NOT NULL,
  `newState` varchar(500) DEFAULT NULL,
  `oldState` varchar(500) DEFAULT NULL,
  `changedBy` bigint DEFAULT NULL,
  PRIMARY KEY (`auditID`),
  KEY `FKnr8an1j44er1adr1cxx1xedsm` (`changedBy`),
  CONSTRAINT `FKnr8an1j44er1adr1cxx1xedsm` FOREIGN KEY (`changedBy`) REFERENCES `User` (`userID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `AuditLog`
--

LOCK TABLES `AuditLog` WRITE;
/*!40000 ALTER TABLE `AuditLog` DISABLE KEYS */;
INSERT INTO `AuditLog` VALUES (1,'CHANGE_PASSWORD','2025-10-22 22:28:44.299307',1,'User','$2a$10$PCs98I6cTaXIR7CnKzw7d.cCjiHmm5SqzNZii2bh9xY3KpDheDCMG','$2a$10$1eBwJBWc/0EQ1A1iljMBaeqk.oLuG2stF2gDnvoTPUCKHVqSarLOq',1),(2,'CHANGE_PASSWORD','2025-10-22 22:34:46.175324',1,'User','$2a$10$.ANuQMJlOmsttPw3UtK05.qLoqYpmWd/TvV/tHVa6ETnyb0CbWQ26','$2a$10$PCs98I6cTaXIR7CnKzw7d.cCjiHmm5SqzNZii2bh9xY3KpDheDCMG',1);
/*!40000 ALTER TABLE `AuditLog` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TournamentParameter`
--

DROP TABLE IF EXISTS `TournamentParameter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `TournamentParameter` (
  `parameterId` int NOT NULL AUTO_INCREMENT,
  `category` varchar(50) DEFAULT NULL,
  `changedBy` varchar(200) DEFAULT NULL,
  `createdAt` datetime(6) DEFAULT NULL,
  `dataType` varchar(20) DEFAULT NULL,
  `description` varchar(200) DEFAULT NULL,
  `effectiveDate` date DEFAULT NULL,
  `isActive` bit(1) NOT NULL,
  `name` varchar(50) NOT NULL,
  `updatedAt` datetime(6) DEFAULT NULL,
  `value` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`parameterId`),
  UNIQUE KEY `UK7ysd1oieumt75ue7jgameomw` (`name`),
  KEY `idx_name` (`name`),
  KEY `idx_category` (`category`),
  KEY `idx_active` (`isActive`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TournamentParameter`
--

LOCK TABLES `TournamentParameter` WRITE;
/*!40000 ALTER TABLE `TournamentParameter` DISABLE KEYS */;
/*!40000 ALTER TABLE `TournamentParameter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Category`
--

DROP TABLE IF EXISTS `Category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Category` (
  `categoryId` bigint NOT NULL AUTO_INCREMENT,
  `categoryName` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `percentage` float DEFAULT NULL,
  PRIMARY KEY (`categoryId`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Category`
--

LOCK TABLES `Category` WRITE;
/*!40000 ALTER TABLE `Category` DISABLE KEYS */;
INSERT INTO `Category` VALUES (1,'Vehículos','Carros, Motos, mototaxis',5),(2,'Joyas','Relojes, aretes, cadenas',4),(3,'Ropa','Ropa de marca',3);
/*!40000 ALTER TABLE `Category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Item`
--

DROP TABLE IF EXISTS `Item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Item` (
  `itemId` bigint NOT NULL AUTO_INCREMENT,
  `brand` varchar(255) DEFAULT NULL,
  `categoryId` bigint DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `nameItem` varchar(255) DEFAULT NULL,
  `specification` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`itemId`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Item`
--

LOCK TABLES `Item` WRITE;
/*!40000 ALTER TABLE `Item` DISABLE KEYS */;
INSERT INTO `Item` VALUES (1,'Dell',3,'Prueba','Prueba','asdfa'),(2,'Cualquiera',2,'asdfasd','Segunda Prueba','asdfasdf');
/*!40000 ALTER TABLE `Item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ItemPhoto`
--

DROP TABLE IF EXISTS `ItemPhoto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ItemPhoto` (
  `itemPhotoId` bigint NOT NULL AUTO_INCREMENT,
  `photoPath` varchar(255) DEFAULT NULL,
  `itemId` bigint DEFAULT NULL,
  PRIMARY KEY (`itemPhotoId`),
  KEY `FKfhtu6pi2nw24nd2tgh4lux5lu` (`itemId`),
  CONSTRAINT `FKfhtu6pi2nw24nd2tgh4lux5lu` FOREIGN KEY (`itemId`) REFERENCES `Item` (`itemId`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ItemPhoto`
--

LOCK TABLES `ItemPhoto` WRITE;
/*!40000 ALTER TABLE `ItemPhoto` DISABLE KEYS */;
INSERT INTO `ItemPhoto` VALUES (1,'https://storage.googleapis.com/solutec-pawn.firebasestorage.app/items/1/2cfcc7ff-c9ff-4a60-ad4d-a4c6e8a0ba1b_86bdbb04ad2c1732.png',1),(2,'https://storage.googleapis.com/solutec-pawn.firebasestorage.app/items/2/eecee225-d76a-433b-85cb-7ce83135a10d_yoriichi_tsugikuni_demon_slayer_kimetsu_no_yaiba_hd_demon_slayer_kimetsu_no_yaiba(2).jpg',2);
/*!40000 ALTER TABLE `ItemPhoto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Loan`
--

DROP TABLE IF EXISTS `Loan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Loan` (
  `balance` decimal(18,2) DEFAULT NULL,
  `defaultDays` int DEFAULT NULL,
  `gracePeriodDays` int DEFAULT NULL,
  `interestRate` decimal(5,2) DEFAULT NULL,
  `latePaymentFee` decimal(10,2) DEFAULT NULL,
  `loanAmount` decimal(18,2) DEFAULT NULL,
  `term` int DEFAULT NULL,
  `approvalDate` datetime(6) DEFAULT NULL,
  `contractGeneratedDate` datetime(6) DEFAULT NULL,
  `dueDate` datetime(6) DEFAULT NULL,
  `loanApplicationId` bigint NOT NULL,
  `loanId` bigint NOT NULL AUTO_INCREMENT,
  `contractNumber` varchar(50) DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `contractSignatureHash` varchar(200) DEFAULT NULL,
  `agreement` longblob,
  `totalInterest` decimal(18,2) DEFAULT NULL COMMENT 'Interés total',
  `totalAmount` decimal(18,2) DEFAULT NULL COMMENT 'Monto total (préstamo + interés)',
  PRIMARY KEY (`loanId`),
  UNIQUE KEY `UK9waid3njpbt9gq4tvlk911ign` (`loanApplicationId`),
  UNIQUE KEY `UKo4jt4v40bkxe2omeuibwfle6o` (`loanApplicationId`),
  UNIQUE KEY `UKj69lqhkd5ge9r5b6p514ffm7t` (`contractNumber`),
  UNIQUE KEY `UKjkky0has529crt53svxy42e68` (`contractNumber`),
  CONSTRAINT `FK57ddsj4d3cqhyi6vdlcdpm5je` FOREIGN KEY (`loanApplicationId`) REFERENCES `LoanApplication` (`loanApplicationID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Loan`
--

LOCK TABLES `Loan` WRITE;
/*!40000 ALTER TABLE `Loan` DISABLE KEYS */;
INSERT INTO `Loan` VALUES (5000.00,90,30,5.00,50.00,5000.00,10,'2025-10-22 21:46:52.181429','2025-10-22 21:46:52.182257','2026-09-17 21:46:52.181470',1,1,'CONT-20251022154652-E31AC2E6','ACTIVO','AC949D47D7F7C0DED71FC1EC2C942EF065F0527D0B4165BA1545996E0583B8BB',NULL,208.33,5208.33),(17500.05,90,30,5.00,50.00,10000.00,15,'2025-10-23 00:16:50.277221','2025-10-23 00:16:50.278063','2027-02-15 00:16:50.277250',2,2,'CONT-20251022181650-3E98CC8B','ACTIVO','E5B1AC35EF3501586709A2596EBDBDFF1DD7E2F0FFABFD59F0F53AC5235C9A2E',NULL,7500.05,17500.05);
/*!40000 ALTER TABLE `Loan` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `LoanApplication`
--

DROP TABLE IF EXISTS `LoanApplication`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `LoanApplication` (
  `loanApplicationID` bigint NOT NULL AUTO_INCREMENT,
  `applicationDate` datetime(6) DEFAULT NULL,
  `approvedAmount` double DEFAULT NULL,
  `clientAccepted` bit(1) DEFAULT NULL,
  `quantityPayments` int DEFAULT NULL,
  `requestedAmount` double DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `itemID` bigint NOT NULL,
  `userID` bigint DEFAULT NULL,
  PRIMARY KEY (`loanApplicationID`),
  UNIQUE KEY `UK49l5btnydyq5jdlgysgt0t2l6` (`itemID`),
  KEY `FKcjdnuugdplrkolkbkdjxdwrvl` (`userID`),
  CONSTRAINT `FKcjdnuugdplrkolkbkdjxdwrvl` FOREIGN KEY (`userID`) REFERENCES `User` (`userID`),
  CONSTRAINT `FKhshfs8ms9kf8oyvkqjjbfa41s` FOREIGN KEY (`itemID`) REFERENCES `Item` (`itemId`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `LoanApplication`
--

LOCK TABLES `LoanApplication` WRITE;
/*!40000 ALTER TABLE `LoanApplication` DISABLE KEYS */;
INSERT INTO `LoanApplication` VALUES (1,'2025-10-22 21:32:21.282275',5000,_binary '',10,5000,'CLIENTE_ACEPTO',1,1),(2,'2025-10-23 00:15:13.414260',10000,_binary '',15,10000,'CLIENTE_ACEPTO',2,1);
/*!40000 ALTER TABLE `LoanApplication` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Notification`
--

DROP TABLE IF EXISTS `Notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Notification` (
  `notificationID` bigint NOT NULL AUTO_INCREMENT,
  `userId` bigint NOT NULL,
  `message` varchar(1000) DEFAULT NULL,
  `sentDate` datetime DEFAULT NULL,
  `readStatus` tinyint(1) DEFAULT '0',
  `type` varchar(50) DEFAULT NULL,
  `relatedEntityType` varchar(50) DEFAULT NULL COMMENT 'LOAN, PAYMENT, PAYMENT_SCHEDULE',
  `relatedEntityId` bigint DEFAULT NULL COMMENT 'ID de la entidad relacionada',
  PRIMARY KEY (`notificationID`),
  KEY `idx_user_read` (`userId`,`readStatus`),
  KEY `idx_type` (`type`),
  CONSTRAINT `fk_notification_user` FOREIGN KEY (`userId`) REFERENCES `User` (`userID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Notification`
--

LOCK TABLES `Notification` WRITE;
/*!40000 ALTER TABLE `Notification` DISABLE KEYS */;
/*!40000 ALTER TABLE `Notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ParameterHistory`
--

DROP TABLE IF EXISTS `ParameterHistory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ParameterHistory` (
  `historyId` bigint NOT NULL AUTO_INCREMENT,
  `action` varchar(50) DEFAULT NULL,
  `changedAt` datetime(6) NOT NULL,
  `changedBy` varchar(200) DEFAULT NULL,
  `newValue` varchar(200) DEFAULT NULL,
  `oldValue` varchar(200) DEFAULT NULL,
  `parameterId` int NOT NULL,
  `parameterName` varchar(50) NOT NULL,
  PRIMARY KEY (`historyId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ParameterHistory`
--

LOCK TABLES `ParameterHistory` WRITE;
/*!40000 ALTER TABLE `ParameterHistory` DISABLE KEYS */;
/*!40000 ALTER TABLE `ParameterHistory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PasswordResetToken`
--

DROP TABLE IF EXISTS `PasswordResetToken`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `PasswordResetToken` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `expiryDate` datetime(6) NOT NULL,
  `token` varchar(255) NOT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKima88norei0uw3te4lim0qsl` (`token`),
  UNIQUE KEY `UKrvxqlsowkgpgscib3isbbosci` (`user_id`),
  CONSTRAINT `FK2v18pceijyp317ympoj7ortmp` FOREIGN KEY (`user_id`) REFERENCES `User` (`userID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PasswordResetToken`
--

LOCK TABLES `PasswordResetToken` WRITE;
/*!40000 ALTER TABLE `PasswordResetToken` DISABLE KEYS */;
/*!40000 ALTER TABLE `PasswordResetToken` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Payment`
--

DROP TABLE IF EXISTS `Payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Payment` (
  `paymentId` bigint NOT NULL AUTO_INCREMENT,
  `amountPaid` decimal(18,2) DEFAULT NULL,
  `paymentDate` datetime(6) DEFAULT NULL,
  `paymentMethod` varchar(200) DEFAULT NULL,
  `paymentNumber` int DEFAULT NULL,
  `reference` longblob,
  `reviewComment` varchar(500) DEFAULT NULL,
  `reviewDate` datetime(6) DEFAULT NULL,
  `reviewedBy` bigint DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `loanID` bigint NOT NULL,
  PRIMARY KEY (`paymentId`),
  KEY `FKlxuk83fmbioxi254jxw8vdvqb` (`loanID`),
  CONSTRAINT `FKlxuk83fmbioxi254jxw8vdvqb` FOREIGN KEY (`loanID`) REFERENCES `Loan` (`loanId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Payment`
--

LOCK TABLES `Payment` WRITE;
/*!40000 ALTER TABLE `Payment` DISABLE KEYS */;
/*!40000 ALTER TABLE `Payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PaymentSchedule`
--

DROP TABLE IF EXISTS `PaymentSchedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `PaymentSchedule` (
  `scheduleId` bigint NOT NULL AUTO_INCREMENT,
  `loanID` bigint NOT NULL,
  `paymentNumber` int NOT NULL,
  `dueDate` date NOT NULL,
  `amountDue` decimal(18,2) NOT NULL,
  `principalAmount` decimal(18,2) NOT NULL,
  `interestAmount` decimal(18,2) NOT NULL,
  `status` varchar(50) DEFAULT 'PENDIENTE' COMMENT 'PENDIENTE, PAGADO, VENCIDO',
  `paidAmount` decimal(18,2) DEFAULT NULL,
  `paidDate` date DEFAULT NULL,
  `notificationSent` tinyint(1) DEFAULT '0',
  `collectorNotified` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`scheduleId`),
  KEY `idx_loan_payment` (`loanID`,`paymentNumber`),
  KEY `idx_due_date` (`dueDate`),
  KEY `idx_status` (`status`),
  KEY `idx_schedule_loanid` (`loanID`),
  CONSTRAINT `fk_paymentschedule_loan` FOREIGN KEY (`loanID`) REFERENCES `Loan` (`loanId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PaymentSchedule`
--

LOCK TABLES `PaymentSchedule` WRITE;
/*!40000 ALTER TABLE `PaymentSchedule` DISABLE KEYS */;
/*!40000 ALTER TABLE `PaymentSchedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Permission`
--

DROP TABLE IF EXISTS `Permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Permission` (
  `permissionId` bigint NOT NULL AUTO_INCREMENT,
  `permissionName` varchar(255) NOT NULL,
  PRIMARY KEY (`permissionId`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Permission`
--

LOCK TABLES `Permission` WRITE;
/*!40000 ALTER TABLE `Permission` DISABLE KEYS */;
INSERT INTO `Permission` VALUES (1,'VIEW_ITEMS'),(2,'CREATE_ORDER'),(3,'VIEW_OWN_LOANS'),(4,'UPLOAD_DOCUMENT'),(5,'MANAGE_USERS'),(6,'MANAGE_ITEMS'),(7,'APPROVE_LOANS'),(8,'VIEW_ALL_ORDERS'),(9,'MANAGE_PROMOTIONS'),(10,'ALL_PERMISSION');
/*!40000 ALTER TABLE `Permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ProposedInstallment`
--

DROP TABLE IF EXISTS `ProposedInstallment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ProposedInstallment` (
  `installmentId` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(38,2) DEFAULT NULL,
  `dueDate` datetime(6) DEFAULT NULL,
  `installmentNumber` int DEFAULT NULL,
  `loanApplicationId` bigint DEFAULT NULL,
  PRIMARY KEY (`installmentId`),
  KEY `FKg2or178cosqx4xxe25gx17grl` (`loanApplicationId`),
  CONSTRAINT `FKg2or178cosqx4xxe25gx17grl` FOREIGN KEY (`loanApplicationId`) REFERENCES `LoanApplication` (`loanApplicationID`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ProposedInstallment`
--

LOCK TABLES `ProposedInstallment` WRITE;
/*!40000 ALTER TABLE `ProposedInstallment` DISABLE KEYS */;
INSERT INTO `ProposedInstallment` VALUES (1,750.00,'2025-11-22 06:00:00.000000',1,1),(2,750.00,'2025-12-22 06:00:00.000000',2,1),(3,750.00,'2026-01-22 06:00:00.000000',3,1),(4,750.00,'2026-02-22 06:00:00.000000',4,1),(5,750.00,'2026-03-22 06:00:00.000000',5,1),(6,750.00,'2026-04-22 06:00:00.000000',6,1),(7,750.00,'2026-05-22 06:00:00.000000',7,1),(8,750.00,'2026-06-22 06:00:00.000000',8,1),(9,750.00,'2026-07-22 06:00:00.000000',9,1),(10,750.00,'2026-08-22 06:00:00.000000',10,1),(11,1166.67,'2025-11-22 06:00:00.000000',1,2),(12,1166.67,'2025-12-22 06:00:00.000000',2,2),(13,1166.67,'2026-01-22 06:00:00.000000',3,2),(14,1166.67,'2026-02-22 06:00:00.000000',4,2),(15,1166.67,'2026-03-22 06:00:00.000000',5,2),(16,1166.67,'2026-04-22 06:00:00.000000',6,2),(17,1166.67,'2026-05-22 06:00:00.000000',7,2),(18,1166.67,'2026-06-22 06:00:00.000000',8,2),(19,1166.67,'2026-07-22 06:00:00.000000',9,2),(20,1166.67,'2026-08-22 06:00:00.000000',10,2),(21,1166.67,'2026-09-22 06:00:00.000000',11,2),(22,1166.67,'2026-10-22 06:00:00.000000',12,2),(23,1166.67,'2026-11-22 06:00:00.000000',13,2),(24,1166.67,'2026-12-22 06:00:00.000000',14,2),(25,1166.67,'2027-01-22 06:00:00.000000',15,2);
/*!40000 ALTER TABLE `ProposedInstallment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Role`
--

DROP TABLE IF EXISTS `Role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Role` (
  `roleId` bigint NOT NULL AUTO_INCREMENT,
  `roleName` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`roleId`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Role`
--

LOCK TABLES `Role` WRITE;
/*!40000 ALTER TABLE `Role` DISABLE KEYS */;
INSERT INTO `Role` VALUES (1,'CLIENTE',NULL),(2,'ADMIN',NULL),(3,'SA',NULL);
/*!40000 ALTER TABLE `Role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `RolePermission`
--

DROP TABLE IF EXISTS `RolePermission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `RolePermission` (
  `permissionID` bigint NOT NULL,
  `roleId` bigint NOT NULL,
  PRIMARY KEY (`permissionID`,`roleId`),
  KEY `fk_rolepermission_role` (`roleId`),
  CONSTRAINT `fk_rolepermission_permission` FOREIGN KEY (`permissionID`) REFERENCES `Permission` (`permissionId`),
  CONSTRAINT `fk_rolepermission_role` FOREIGN KEY (`roleId`) REFERENCES `Role` (`roleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `RolePermission`
--

LOCK TABLES `RolePermission` WRITE;
/*!40000 ALTER TABLE `RolePermission` DISABLE KEYS */;
INSERT INTO `RolePermission` VALUES (1,1),(2,1),(3,1),(4,1),(1,2),(2,2),(3,2),(4,2),(5,2),(6,2),(7,2),(8,2),(9,2),(1,3),(2,3),(3,3),(4,3),(5,3),(6,3),(7,3),(8,3),(9,3),(10,3);
/*!40000 ALTER TABLE `RolePermission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Task`
--

DROP TABLE IF EXISTS `Task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Task` (
  `taskId` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `dueDate` datetime(6) DEFAULT NULL,
  `entityType` varchar(255) DEFAULT NULL,
  `priority` varchar(255) DEFAULT NULL,
  `relatedEntityID` bigint DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `assignedTo` bigint DEFAULT NULL,
  PRIMARY KEY (`taskId`),
  KEY `FKlwiw9tffjmbou55i2hxc80dea` (`assignedTo`),
  CONSTRAINT `FKlwiw9tffjmbou55i2hxc80dea` FOREIGN KEY (`assignedTo`) REFERENCES `User` (`userID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Task`
--

LOCK TABLES `Task` WRITE;
/*!40000 ALTER TABLE `Task` DISABLE KEYS */;
/*!40000 ALTER TABLE `Task` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `User`
--

DROP TABLE IF EXISTS `User`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `User` (
  `roleID` bigint DEFAULT NULL,
  `userID` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `firstLastName` varchar(255) NOT NULL,
  `firstName` varchar(255) NOT NULL,
  `marriedLastName` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `secondLastName` varchar(255) DEFAULT NULL,
  `secondOrMoreNames` varchar(255) DEFAULT NULL,
  `telephone` varchar(255) DEFAULT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`userID`),
  UNIQUE KEY `UKe6gkqunxajvyxl5uctpl2vl2p` (`email`),
  UNIQUE KEY `UKjreodf78a7pl5qidfh43axdfb` (`username`),
  KEY `fk_user_role` (`roleID`),
  CONSTRAINT `fk_user_role` FOREIGN KEY (`roleID`) REFERENCES `Role` (`roleId`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `User`
--

LOCK TABLES `User` WRITE;
/*!40000 ALTER TABLE `User` DISABLE KEYS */;
INSERT INTO `User` VALUES (3,1,'Km. 43.5, Callejón Don Adelmo, Calle Escuela #2, Aldea El Jocotillo','wilmerantoniosinay@gmail.com','Sinay','Wilmer','','$2a$10$.ANuQMJlOmsttPw3UtK05.qLoqYpmWd/TvV/tHVa6ETnyb0CbWQ26','','Antonio','+50230472670','wilmer');
/*!40000 ALTER TABLE `User` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `valuation`
--

DROP TABLE IF EXISTS `valuation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `valuation` (
  `valuationID` int NOT NULL AUTO_INCREMENT,
  `appraiserID` int DEFAULT NULL,
  `comments` varchar(500) DEFAULT NULL,
  `estimatedValue` decimal(18,2) DEFAULT NULL,
  `valuationDate` datetime(6) DEFAULT NULL,
  `loanApplicationID` bigint NOT NULL,
  PRIMARY KEY (`valuationID`),
  KEY `FK7ftpqgdn7mmv7w7c9gd6gwyv1` (`loanApplicationID`),
  CONSTRAINT `FK7ftpqgdn7mmv7w7c9gd6gwyv1` FOREIGN KEY (`loanApplicationID`) REFERENCES `LoanApplication` (`loanApplicationID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `valuation`
--

LOCK TABLES `valuation` WRITE;
/*!40000 ALTER TABLE `valuation` DISABLE KEYS */;
/*!40000 ALTER TABLE `valuation` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-22 23:19:01
