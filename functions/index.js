const { onDocumentCreated } = require("firebase-functions/v2/firestore");
const { initializeApp } = require("firebase-admin/app");
const { getMessaging } = require("firebase-admin/messaging");
const logger = require("firebase-functions/logger");

initializeApp();

// Cloud Function que se ejecuta cuando se crea una nueva rutina predefinida
exports.notifyNewPredefinedRoutine = onDocumentCreated("rutinasPredefinidas/{docId}", (event) => {
  const data = event.data.data();
  const nombreRutina = data?.nombreRutina || "una rutina";

  const message = {
    notification: {
      title: "💪 ¡Nueva rutina disponible!",
      body: `Se ha publicado ${nombreRutina}`,
    },
    topic: "nuevas_rutinas",
  };

  return getMessaging().send(message)
    .then((response) => {
      logger.info("✅ Notificación enviada con éxito:", response);
    })
    .catch((error) => {
      logger.error("❌ Error al enviar la notificación:", error);
    });
});
