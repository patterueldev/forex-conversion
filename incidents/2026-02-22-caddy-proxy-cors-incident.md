# Incident Report: CORS Preflight Failure due to Caddy/OpenProject proxy

Date: 2026-02-22
Status: Open
Severity: High

Summary
-------
During local development the web frontend (http://localhost:5173) receives a CORS preflight failure when calling the backend at http://thursday.local:8080/convert. The browser's OPTIONS preflight returns a 400 Bad Request with a `Via: 1.1 Caddy` header, and the response lacks `Access-Control-Allow-Origin`, causing the browser to block the request. Mobile clients are not affected.

Key findings
------------
- Browser error observed: "No 'Access-Control-Allow-Origin' header is present on the requested resource" and net::ERR_FAILED for POST to /convert.
- Direct curl to the Ktor server (port 8080) returns proper CORS headers (e.g. `Access-Control-Allow-Origin: *`).
- Browser devtools show OPTIONS response with `Via: 1.1 Caddy` and HTTP/1.1 400 Bad Request — the proxy is rejecting or mishandling the preflight before it reaches Ktor.
- `docker ps` shows an `openproject/proxy` (Caddy) container binding 127.0.0.1:8080 -> 80, which conflicts with the backend port.
- Stopping the OpenProject proxy container restores the web client's ability to successfully call /convert (temporary mitigation verified).

Root cause
----------
A local Caddy reverse proxy (part of an OpenProject Docker container) was bound to host port 8080 and intercepted preflight OPTIONS requests; it either rejected OPTIONS or did not forward/attach the required CORS headers, preventing the browser from completing the cross-origin request. The Ktor server itself responds correctly to preflight when accessed directly, so the proxy was the blocker.

Impact
------
- Web frontend development and testing blocked by CORS errors.
- Mobile applications and non-browser clients are unaffected.
- Time lost triaging and reconfiguring local environment.

Immediate mitigation (temporary)
-------------------------------
- Stop the OpenProject proxy container to free port 8080 and allow direct access to the backend:
  - `docker ps | grep openproject` to find the container
  - `docker stop <container_id>` or `docker-compose -f <compose-file> down`
- Or change the backend to run on a different port (e.g., 8081) and update the frontend base URL to use that port.

Permanent remediation options
----------------------------
- Reconfigure the Caddy proxy in the OpenProject container to forward OPTIONS requests and to set or forward appropriate CORS response headers (e.g., `Access-Control-Allow-Origin`, `Access-Control-Allow-Methods`, `Access-Control-Allow-Headers`).
- Change the OpenProject proxy's host port mapping to avoid using 8080 in developer machines.
- Document and reserve development ports (add to repo README) to avoid port conflicts.

Steps to reproduce
------------------
1. Run the OpenProject proxy (or any Caddy instance) bound to 127.0.0.1:8080.
2. Start frontend dev server at http://localhost:5173.
3. Trigger the POST /convert call from the web UI.
4. Observe OPTIONS preflight returns 400 with `Via: 1.1 Caddy` and no `Access-Control-Allow-Origin`.

Investigation commands used
--------------------------
- `curl -v -X OPTIONS 'http://thursday.local:8080/convert' -H 'Origin: http://localhost:5173' -H 'Access-Control-Request-Method: POST' -H 'Access-Control-Request-Headers: Content-Type'`
- `docker ps | grep openproject`
- `lsof -i :8080`
- `ping thursday.local`

Owner / next steps
-------------------
- Owner: dev / ops (assign to project owner)
- Short-term: stop the OpenProject proxy while developing or change backend port.
- Long-term: update Caddy configuration or docker-compose to avoid blocking preflight and add guidance to developer docs.

Notes
-----
This incident was validated interactively: direct server hits return correct CORS headers while browser traffic was blocked by a proxy layer; stopping the OpenProject proxy immediately fixed the problem for the web client.

