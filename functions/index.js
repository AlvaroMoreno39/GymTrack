/*
 Archivo: index.js
 Lenguaje: JavaScript (Node.js)
 Ubicación: Firebase Cloud Functions
 Descripción:
 Este archivo define una función en la nube (Cloud Function) que escucha automáticamente 
 cuando se crea un nuevo documento en la colección 'rutinasPredefinidas' de Firestore 
 y envía una notificación push a todos los usuarios suscritos al topic 'nuevas_rutinas' 
 usando Firebase Cloud Messaging. También registra mensajes de éxito o error en los logs 
 para poder hacer seguimiento.
*/

// Importa la función onDocumentCreated del módulo de Firebase Functions v2 para Firestore.
// Sirve para ejecutar una función automáticamente cuando se crea un nuevo documento en una colección.
const { onDocumentCreated } = require("firebase-functions/v2/firestore");

// Importa initializeApp del módulo de Firebase Admin SDK.
// Este módulo inicializa la app admin para tener acceso a los servicios de Firebase (como mensajería).
const { initializeApp } = require("firebase-admin/app");

// Importa getMessaging del módulo de Firebase Admin SDK.
// Esta función se usa para enviar mensajes (notificaciones push) a dispositivos suscritos.
const { getMessaging } = require("firebase-admin/messaging");

// Importa logger del módulo de Firebase Functions.
// Permite registrar mensajes en la consola de logs para depuración y control.
const logger = require("firebase-functions/logger");

// Inicializa la app de Firebase Admin para poder usar sus servicios (como enviar notificaciones).
initializeApp();

// Define y exporta una función de Cloud llamada notifyNewPredefinedRoutine.
// Esta función se dispara automáticamente cuando se crea un nuevo documento en la colección 'rutinasPredefinidas'.
exports.notifyNewPredefinedRoutine = onDocumentCreated("rutinasPredefinidas/{docId}", (event) => {
  
  // Obtiene los datos del documento recién creado.
  const data = event.data.data();
  
  // Obtiene el nombre de la rutina, o usa un valor por defecto si no existe.
  const nombreRutina = data?.nombreRutina || "una rutina";

  // Construye el objeto de mensaje a enviar.
  // Incluye tanto notification (para mostrar en la bandeja del dispositivo) como data (para manejar en segundo plano).
  const message = {
    notification: {
      title: "💪 ¡Nueva rutina disponible!",
      body: `Se ha publicado ${nombreRutina}`,
    },
    data: {
      title: "💪 ¡Nueva rutina disponible!",
      body: `Se ha publicado ${nombreRutina}`,
    },
    topic: "nuevas_rutinas", // El mensaje se envía a todos los dispositivos suscritos al topic 'nuevas_rutinas'.
  };

  // Envía el mensaje a través de Firebase Cloud Messaging.
  return getMessaging().send(message)
    .then((response) => {
      // Si el envío es exitoso, registra un mensaje en los logs de éxito.
      logger.info("✅ Notificación enviada con éxito:", response);
    })
    .catch((error) => {
      // Si ocurre un error, registra un mensaje en los logs de error.
      logger.error("❌ Error al enviar la notificación:", error);
    });
});
