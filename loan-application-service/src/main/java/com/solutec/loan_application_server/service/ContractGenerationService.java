package com.solutec.loan_application_server.service;

import com.solutec.loan_application_server.entity.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class ContractGenerationService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public String generateContractNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "CONT-" + timestamp + "-" + uuid;
    }

    public String generateSignatureHash(String contractNumber, Long userId, LocalDateTime timestamp) {
        try {
            String data = contractNumber + userId + timestamp.toString();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString().toUpperCase();
        } catch (Exception e) {
            return UUID.randomUUID().toString().replace("-", "").toUpperCase();
        }
    }


    public String generateContractHtml(
            Loan loan,
            LoanApplication application,
            User user,
            Item item,
            List<ProposedInstallment> installments) {

        String contractNumber = loan.getContractNumber();
        String signatureHash = loan.getContractSignatureHash();
        LocalDateTime now = LocalDateTime.now();

        BigDecimal totalToPay = installments.stream()
                .map(ProposedInstallment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        StringBuilder installmentTable = new StringBuilder();
        for (ProposedInstallment inst : installments) {
            installmentTable.append(String.format("""
                <tr>
                    <td style="padding:8px; border:1px solid #ddd; text-align:center;">%d</td>
                    <td style="padding:8px; border:1px solid #ddd; text-align:right;">GTQ %.2f</td>
                    <td style="padding:8px; border:1px solid #ddd; text-align:center;">%s</td>
                </tr>
                """,
                    inst.getInstallmentNumber(),
                    inst.getAmount(),
                    inst.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            ));
        }

        return String.format("""
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Contrato de Empeño #%s</title>
    <style>
        @media print {
            .no-print { display: none; }
            body { margin: 0; }
        }
        body {
            font-family: 'Times New Roman', serif;
            max-width: 900px;
            margin: 20px auto;
            padding: 40px;
            background: white;
            line-height: 1.6;
            color: #000;
        }
        .header {
            text-align: center;
            border-bottom: 3px double #000;
            padding-bottom: 20px;
            margin-bottom: 30px;
        }
        .header h1 {
            font-size: 24px;
            margin: 10px 0;
            text-transform: uppercase;
        }
        .contract-number {
            font-size: 14px;
            font-weight: bold;
            color: #c00;
        }
        .section {
            margin: 20px 0;
            text-align: justify;
        }
        .section-title {
            font-weight: bold;
            text-decoration: underline;
            margin-top: 25px;
            margin-bottom: 10px;
        }
        table {
            width: 100%%;
            border-collapse: collapse;
            margin: 15px 0;
        }
        th {
            background-color: #f0f0f0;
            padding: 10px;
            border: 1px solid #000;
            font-weight: bold;
        }
        td {
            padding: 8px;
            border: 1px solid #ddd;
        }
        .signature-section {
            margin-top: 60px;
            page-break-inside: avoid;
        }
        .signature-box {
            width: 45%%;
            display: inline-block;
            text-align: center;
            margin-top: 50px;
        }
        .signature-line {
            border-top: 2px solid #000;
            margin-top: 40px;
            padding-top: 5px;
        }
        .hash-box {
            background: #f9f9f9;
            border: 1px solid #ccc;
            padding: 15px;
            margin: 20px 0;
            font-family: 'Courier New', monospace;
            word-break: break-all;
            font-size: 11px;
        }
        .important {
            background: #fff3cd;
            border-left: 4px solid #ffc107;
            padding: 15px;
            margin: 20px 0;
        }
        @page {
            margin: 2cm;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>CONTRATO DE PRÉSTAMO PRENDARIO</h1>
        <p class="contract-number">Contrato N°: %s</p>
        <p>Casa de Empeños "SOLUTEC LOAN SERVICE"</p>
        <p style="font-size:12px;">Guatemala, %s</p>
    </div>

    <div class="section">
        <p><strong>COMPARECEN:</strong></p>
        <p>
            Por una parte, <strong>"SOLUTEC LOAN SERVICE"</strong>, casa de empeños debidamente constituida, 
            representada en este acto por su representante legal, quien en adelante se denominará 
            <strong>"EL ACREEDOR"</strong>; y por la otra parte:
        </p>
        <p>
            <strong>%s %s %s</strong>, mayor de edad, con Documento Personal de Identificación (DPI), 
            domiciliado en <strong>%s</strong>, correo electrónico <strong>%s</strong>, 
            quien en adelante se denominará <strong>"EL DEUDOR"</strong>.
        </p>
    </div>

    <div class="section">
        <p class="section-title">PRIMERA: OBJETO DEL CONTRATO</p>
        <p>
            Por medio del presente contrato, <strong>EL ACREEDOR</strong> otorga a <strong>EL DEUDOR</strong> 
            un préstamo de dinero en efectivo por la suma de <strong>GTQ %.2f (QUETZALES EXACTOS)</strong>, 
            cantidad que <strong>EL DEUDOR</strong> declara recibir a su entera satisfacción en este acto.
        </p>
    </div>

    <div class="section">
        <p class="section-title">SEGUNDA: GARANTÍA PRENDARIA</p>
        <p>
            Como garantía del préstamo otorgado, <strong>EL DEUDOR</strong> entrega en prenda a 
            <strong>EL ACREEDOR</strong> el siguiente bien mueble:
        </p>
        <table>
            <tr>
                <th>Descripción</th>
                <th>Marca</th>
                <th>Especificaciones</th>
            </tr>
            <tr>
                <td><strong>%s</strong></td>
                <td>%s</td>
                <td>%s</td>
            </tr>
        </table>
        <p>
            <strong>EL DEUDOR</strong> declara bajo juramento que el bien entregado en prenda es de su 
            legítima propiedad, está libre de gravámenes, y tiene plena facultad para constituir la presente garantía.
        </p>
    </div>

    <div class="section">
        <p class="section-title">TERCERA: PLAZO Y FORMA DE PAGO</p>
        <p>
            El préstamo será pagado en <strong>%d (CUOTAS)</strong> pagos mensuales consecutivos, 
            con una tasa de interés del <strong>%.2f%% mensual</strong>, conforme al siguiente plan de cuotas:
        </p>
        <table>
            <thead>
                <tr>
                    <th>Cuota N°</th>
                    <th>Monto a Pagar</th>
                    <th>Fecha de Vencimiento</th>
                </tr>
            </thead>
            <tbody>
                %s
            </tbody>
            <tfoot>
                <tr style="font-weight:bold; background:#f0f0f0;">
                    <td style="text-align:right;" colspan="1">TOTAL A PAGAR:</td>
                    <td style="text-align:right;">GTQ %.2f</td>
                    <td></td>
                </tr>
            </tfoot>
        </table>
    </div>

    <div class="section">
        <p class="section-title">CUARTA: MORA Y CARGOS POR ATRASO</p>
        <p>
            En caso de mora en el pago de cualquiera de las cuotas, <strong>EL DEUDOR</strong> deberá pagar 
            un cargo adicional de <strong>GTQ %.2f</strong> por cada día de atraso, más los intereses 
            moratorios correspondientes.
        </p>
        <p>
            <strong>EL DEUDOR</strong> cuenta con un período de gracia de <strong>%d días</strong> posteriores 
            a la fecha de vencimiento de cada cuota sin aplicación de cargos adicionales.
        </p>
    </div>

    <div class="section">
        <p class="section-title">QUINTA: INCUMPLIMIENTO Y EJECUCIÓN DE LA PRENDA</p>
        <p>
            Si <strong>EL DEUDOR</strong> incumple con el pago de las cuotas por un período mayor a 
            <strong>%d días</strong> consecutivos, <strong>EL ACREEDOR</strong> podrá proceder a la ejecución 
            de la garantía prendaria, mediante la venta del bien empeñado para recuperar el capital prestado, 
            los intereses devengados y los gastos de cobranza.
        </p>
        <p>
            <strong>EL DEUDOR</strong> manifiesta su conformidad expresa con lo establecido en esta cláusula 
            y reconoce que la venta del bien se realizará conforme a la legislación vigente.
        </p>
    </div>

    <div class="section important">
        <p style="margin:0;"><strong>IMPORTANTE:</strong></p>
        <p style="margin:5px 0 0 0;">
            El incumplimiento del presente contrato puede resultar en la pérdida definitiva del bien empeñado. 
            <strong>EL DEUDOR</strong> declara haber leído, comprendido y aceptado todos los términos y 
            condiciones establecidos en este documento.
        </p>
    </div>

    <div class="section">
        <p class="section-title">SEXTA: ACEPTACIÓN Y FIRMA</p>
        <p>
            En prueba de conformidad y aceptación de todas las cláusulas anteriores, ambas partes suscriben 
            el presente contrato en la ciudad de Guatemala, el día <strong>%s</strong> a las 
            <strong>%s</strong> horas.
        </p>
    </div>

    <div class="hash-box">
        <strong>FIRMA ELECTRÓNICA (HASH SHA-256):</strong><br>
        %s
    </div>

    <div class="signature-section">
        <div class="signature-box" style="float:left;">
            <div class="signature-line">
                <strong>EL ACREEDOR</strong><br>
                Solutec Loan Service<br>
                Representante Legal
            </div>
        </div>
        <div class="signature-box" style="float:right;">
            <div class="signature-line">
                <strong>EL DEUDOR</strong><br>
                %s %s<br>
                Usuario: %s
            </div>
        </div>
        <div style="clear:both;"></div>
    </div>

    <div class="no-print" style="text-align:center; margin-top:40px;">
        <button onclick="window.print()" style="
            background:#28a745;
            color:white;
            border:none;
            padding:15px 30px;
            font-size:16px;
            border-radius:5px;
            cursor:pointer;
        ">
            Imprimir Contrato
        </button>
    </div>
</body>
</html>
                """,
                contractNumber, // título
                contractNumber, // número en header
                now.format(DATE_FORMATTER), // fecha
                user.getFirstName(), user.getFirstLastName(), user.getSecondLastName() != null ? user.getSecondLastName() : "",
                user.getAddress() != null ? user.getAddress() : "Dirección no proporcionada",
                user.getEmail(),
                loan.getLoanAmount(), // monto prestado
                item.getNameItem(), // descripción del artículo
                item.getBrand() != null ? item.getBrand() : "N/A",
                item.getSpecification() != null ? item.getSpecification() : "Ver descripción completa en anexos",
                loan.getTerm(), // número de cuotas
                loan.getInterestRate(), // tasa de interés
                installmentTable.toString(), // tabla de cuotas
                totalToPay, // total a pagar
                loan.getLatePaymentFee(), // mora
                loan.getGracePeriodDays(), // días de gracia
                loan.getDefaultDays(), // días antes de incumplimiento
                now.format(DATE_FORMATTER), // fecha de firma
                now.format(TIME_FORMATTER), // hora de firma
                signatureHash, // hash de firma
                user.getFirstName(), user.getFirstLastName(),
                user.getUsername() // usar como DPI temporal
        );
    }
}